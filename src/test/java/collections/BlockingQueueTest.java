package collections;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/*
 * BlockingQueue 面试核心：
 *
 * 1. BlockingQueue 是 java.util.concurrent 下的线程安全队列，最典型用途是生产者-消费者模型。
 *    生产者往队列放任务，消费者从队列取任务，队列负责线程间安全交接。
 *
 * 2. BlockingQueue 的关键不只是“线程安全”，而是“阻塞语义”：
 *    - put(e)：队列满了就等待，直到有空间。
 *    - take()：队列空了就等待，直到有元素。
 *    - offer(e)：立即尝试插入，满了返回 false，不等待。
 *    - poll()：立即尝试获取，空了返回 null，不等待。
 *    - offer(e, timeout, unit)：满了最多等 timeout，超时返回 false。
 *    - poll(timeout, unit)：空了最多等 timeout，超时返回 null。
 *
 * 3. 面试常问的 trade-off：
 *    - ArrayBlockingQueue：数组实现，容量固定，内存稳定，适合明确背压。
 *    - LinkedBlockingQueue：链表实现，可以指定容量；不指定时默认容量接近 Integer.MAX_VALUE，
 *      生产快于消费时可能堆积大量节点，带来内存压力甚至 OOM。
 *
 * 4. BlockingQueue 不允许 null。
 *    因为 poll() 在队列为空时会返回 null，如果允许存 null，就无法区分“取到了 null”还是“队列为空”。
 *
 * 5. 正确处理中断：
 *    put/take 会一直阻塞等待条件满足；等待期间如果线程被 interrupt，会抛 InterruptedException。
 *    带 timeout 的 offer/poll 超时后返回 false/null；等待期间如果线程被 interrupt，也会抛 InterruptedException。
 *    如果捕获后不继续抛出，通常要调用 Thread.currentThread().interrupt() 恢复中断标记。
 */
class BlockingQueueTest {

    /*
     * ArrayBlockingQueue 面试点：
     *
     * 1. 它是有界阻塞队列，构造时必须指定容量。
     * 这里容量是 2，所以最多同时放 2 个元素。
     *
     * 2. put() 和 take() 是“死等型”API：
     * - put：如果队列满了，当前线程会阻塞，直到消费者 take/poll 走元素。
     * - take：如果队列空了，当前线程会阻塞，直到生产者 put/offer 进元素。
     *
     * 3. remainingCapacity() 表示当前还剩多少容量。
     * 它常用于观察状态，但在多线程环境下只能当作瞬时值，不能依赖它做严格并发判断。
     *
     * 4. offer(e, timeout, unit) 和 poll(timeout, unit) 是“限时等待型”API：
     * - offer：满了最多等一段时间，仍然没空间就返回 false。
     * - poll：空了最多等一段时间，仍然没元素就返回 null。
     *
     * 5. ArrayBlockingQueue 的典型优势是固定容量带来的背压：
     * 生产太快时生产者会被迫等待，不会无限堆积任务。
     */
    @Test
    void arrayBlockingQueuePutTake() throws Exception {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        queue.put("job-1");
        queue.put("job-2");
        assertEquals("job-1", queue.take());
        assertEquals(1, queue.remainingCapacity());
        queue.offer("job-3", 100, TimeUnit.MILLISECONDS);
        assertEquals("job-2", queue.poll(1, TimeUnit.SECONDS));
    }

    /*
     * 生产者-消费者模型面试点：
     *
     * 1. BlockingQueue 可以把“数据结构”和“线程协调”合在一起：
     * 生产者只管 put，消费者只管 take，不需要手写 wait/notify。
     *
     * 2. 这里用 LinkedBlockingQueue<>(3)：
     * - 指定容量 3，表示最多积压 3 个元素。
     * - 如果不指定容量，LinkedBlockingQueue 默认容量是 Integer.MAX_VALUE，
     * 面试时通常要提醒这不是严格意义上的安全无界，任务堆积时可能 OOM。
     *
     * 3. LinkedBlockingQueue 与 ArrayBlockingQueue 的区别：
     * - LinkedBlockingQueue 基于链表节点，put/take 通常用两把锁，生产和消费并发度更好。
     * - 代价是每个元素都要额外节点对象，内存和 GC 压力更大。
     * - ArrayBlockingQueue 基于固定数组，内存更稳定，但 put/take 竞争相对集中。
     */
    @Test
    void producerConsumerWithBlockingQueue() throws Exception {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(3);
        CountDownLatch consumed = new CountDownLatch(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    queue.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    assertEquals(i, queue.take());
                    consumed.countDown();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        producer.start();
        producer.join(2000);
        consumer.join(2000);
        assertTrue(consumed.await(1, TimeUnit.SECONDS));
    }
}
