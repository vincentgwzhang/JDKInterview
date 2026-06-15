package jdk19;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.FailedException;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.StructuredTaskScope.Subtask.State.FAILED;
import static java.util.concurrent.StructuredTaskScope.Subtask.State.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

/*
 * StructuredTaskScope 面试核心：
 *
 * 1. JDK19 引入结构化并发的预览/孵化方向；后续 JDK 的 API 多次演进。
 *    这个测试使用的是当前项目 JDK 配置下的 StructuredTaskScope API。
 *
 * 2. 结构化并发解决的问题不是“让计算更快”，而是“管理一组相关子任务”：
 *    - 子任务属于同一个 scope。
 *    - 父任务 join 时统一等待。
 *    - 子任务失败时由 scope 统一感知和传播。
 *    - 离开 scope 时不留下孤儿任务。
 *
 * 3. 可以把它理解成并发版 try-with-resources：
 *    try 代码块定义了子任务的生命周期边界。
 *
 * 4. 当前 API 里的 5 个常见 Joiner 策略：
 *
 *    - allSuccessfulOrThrow()
 *      所有子任务都成功才算成功；join() 返回 List<T>。
 *      适合“我要所有成功结果”的场景。
 *
 *    - anySuccessfulOrThrow()
 *      任意一个子任务成功就算成功；join() 返回一个 T。
 *      适合“多个候选源，谁先成功用谁”的场景。
 *
 *    - awaitAllSuccessfulOrThrow()
 *      所有子任务都成功才算成功；join() 返回 null。
 *      结果必须靠你提前保存的 Subtask<T>，然后调用 subtask.get() 取。
 *      适合“我要按 user/order/coupon 这种业务字段分别取结果”的场景。
 *
 *    - awaitAll()
 *      等所有子任务结束，不管成功还是失败；join() 返回 null。
 *      成功值、异常、状态都必须靠每个 Subtask 自己取：
 *      subtask.state() / subtask.get() / subtask.exception()。
 *      StructuredTaskScope.open() 不传 Joiner 时，默认就是类似 awaitAll() 的语义。
 *
 *    - allUntil(predicate)
 *      每个子任务完成时都交给 predicate 判断，满足条件就停止等待；join() 返回 List<Subtask<T>>。
 *      因为它可能在部分成功、部分失败、部分未完成时停止，所以返回的是 Subtask 列表，
 *      需要你自己检查 state/get/exception。
 *
 * 5. 返回值记忆：
 *
 *    - join() 返回 List<T>：allSuccessfulOrThrow()
 *    - join() 返回 T：anySuccessfulOrThrow()
 *    - join() 返回 null，靠保存的 Subtask 取值：awaitAllSuccessfulOrThrow()、awaitAll()、open() 默认策略
 *    - join() 返回 List<Subtask<T>>：allUntil(predicate)
 */
class StructuredTaskScopeConceptTest {

    /*
     * Joiner.allSuccessfulOrThrow() 知识点：
     *
     * 1. 适合“多个子任务都必须成功，并且我想直接拿到所有结果”的场景。
     * 2. 所有子任务都成功：scope.join() 返回 List<T>。
     * 3. 任一子任务失败：scope.join() 抛 FailedException。
     * 4. 返回的 List 表示所有成功结果，但面试时不要依赖它表达业务字段含义；
     *    如果要区分 user/order/coupon，更清楚的方式是保存各自的 Subtask 引用。
     */
    @Test
    void allSuccessfulJoinerReturnsAllResults() throws Exception {
        try (var scope = StructuredTaskScope.open(Joiner.<String>allSuccessfulOrThrow())) {
            scope.fork(() -> loadUser());
            scope.fork(() -> loadOrder());
            scope.fork(() -> loadCoupon());

            List<String> results = scope.join();

            assertEquals(3, results.size());
            assertEquals(Set.of("user", "order", "coupon"), Set.copyOf(results));
        }
    }

    /*
     * Joiner.anySuccessfulOrThrow() 知识点：
     *
     * 1. 适合“多个候选任务里，任意一个成功就够了”的场景。
     *    例如请求多个镜像服务、多个缓存源、多个副本，谁先成功用谁。
     *
     * 2. 至少一个子任务成功：scope.join() 返回某一个成功结果。
     * 3. 所有子任务都失败：scope.join() 才抛 FailedException。
     * 4. 这个策略表达的是“竞速成功”，不是“收集所有成功结果”。
     */
    @Test
    void anySuccessfulJoinerReturnsOneSuccessfulResult() throws Exception {
        try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow())) {
            scope.fork(() -> loadOrderWithFailure());
            scope.fork(() -> loadUser());

            String result = scope.join();

            assertEquals("user", result);
        }
    }

    /*
     * Joiner.awaitAllSuccessfulOrThrow() 知识点：
     *
     * 1. 适合“所有子任务都必须成功，但结果我要自己按业务字段取”的场景。
     * 2. 和 allSuccessfulOrThrow() 的区别：
     *    - allSuccessfulOrThrow()：join() 返回 List<T>。
     *    - awaitAllSuccessfulOrThrow()：join() 返回 null，结果从 Subtask.get() 取。
     * 3. 任一子任务失败时，join() 抛 FailedException。
     */
    @Test
    void awaitAllSuccessfulJoinerWaitsAndResultsComeFromSubtasks() throws Exception {
        try (var scope = StructuredTaskScope.<String, Void>open(Joiner.awaitAllSuccessfulOrThrow())) {
            Subtask<String> user = scope.fork(() -> loadUser());
            Subtask<String> order = scope.fork(() -> loadOrder());
            Subtask<String> coupon = scope.fork(() -> loadCoupon());

            assertNull(scope.join());

            assertEquals("user-order-coupon", user.get() + "-" + order.get() + "-" + coupon.get());
        }
    }

    /*
     * Joiner.awaitAll() 知识点：
     *
     * 1. 适合“无论成功失败，我都要等所有已经 fork 的子任务结束，然后自己检查状态”的场景。
     * 2. 子任务失败时，join() 不会自动抛 FailedException。
     * 3. join() 后通过 subtask.state()/get()/exception() 自己判断 SUCCESS 或 FAILED。
     * 4. 这适合部分成功、错误汇总、降级返回等场景。
     */
    @Test
    void awaitAllJoinerLetsCallerInspectSuccessAndFailure() throws Exception {
        try (var scope = StructuredTaskScope.<String, Void>open(Joiner.awaitAll())) {
            Subtask<String> user = scope.fork(() -> loadUser());
            Subtask<String> order = scope.fork(() -> loadOrderWithFailure());

            assertNull(scope.join());

            assertEquals(SUCCESS, user.state());
            assertEquals("user", user.get());
            assertEquals(FAILED, order.state());
            assertInstanceOf(IllegalStateException.class, order.exception());
        }
    }

    /*
     * Joiner.allUntil(predicate) 知识点：
     *
     * 1. 适合自定义“什么时候够了，就停止等待”的规则。
     * 2. 每个子任务完成时，Joiner 都会把这个 Subtask 交给 predicate 判断。
     * 3. predicate 返回 true，表示条件达成，可以结束等待，并取消还没完成的兄弟任务。
     * 4. join() 返回 List<Subtask<T>>，不是 List<T>，因为这些 subtask 里可能既有成功也有失败。
     *
     * 下面的例子表达“失败达到 2 个就停”。
     */
    @Test
    void allUntilJoinerStopsWhenCustomPredicateIsSatisfied() throws Exception {
        AtomicInteger failures = new AtomicInteger();
        var stopAfterTwoFailures = Joiner.<String>allUntil(task -> {
            if (task.state() != FAILED) {
                return false;
            }
            return failures.incrementAndGet() >= 2;
        });

        try (var scope = StructuredTaskScope.open(stopAfterTwoFailures)) {
            scope.fork(() -> loadUser());
            scope.fork(() -> loadOrderWithFailure());
            scope.fork(() -> loadCouponWithFailure());

            List<Subtask<String>> completed = scope.join();

            assertTrue(failures.get() >= 2);
            assertTrue(completed.stream().filter(task -> task.state() == FAILED).count() >= 2);
        }
    }

    /*
     * 子任务失败场景：
     *
     * 如果 loadOrder() 抛异常，这个异常不会“悄悄丢在子线程里”。
     * scope.join() 会把失败统一传播给父任务。
     *
     * 这就是结构化并发比手写 Future 更清晰的地方：
     * 父作用域知道哪一组子任务属于自己，也知道它们是否整体成功。
     */
    @Test
    void failedSubtaskIsReportedByScopeJoin() {
        try (var scope = StructuredTaskScope.<String, Void>open(Joiner.awaitAllSuccessfulOrThrow())) {
            scope.fork(() -> loadUser());
            Subtask<String> order = scope.fork(() -> loadOrderWithFailure());
            scope.fork(() -> loadCoupon());

            FailedException failed = assertThrows(FailedException.class, scope::join);

            assertEquals(FAILED, order.state());
            assertInstanceOf(IllegalStateException.class, order.exception());
            assertInstanceOf(IllegalStateException.class, failed.getCause());
        }
    }

    private static String loadUser() {
        IO.println("loadUser function was triggered");
        return "user";
    }

    private static String loadOrder() {
        IO.println("loadOrder function was triggered");
        return "order";
    }

    private static String loadOrderWithFailure() {
        IO.println("loadOrderWithFailure function was triggered");
        throw new IllegalStateException("order service down");
    }

    private static String loadCoupon() {
        IO.println("loadCoupon function was triggered");
        return "coupon";
    }

    private static String loadCouponWithFailure() {
        IO.println("loadCouponWithFailure function was triggered");
        throw new IllegalStateException("coupon service down");
    }
}
