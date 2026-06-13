package threading;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {

    @Test
    void threadPoolExecutorRunsTasks() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        try (ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2, 2,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy())) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                futures.add(pool.submit(counter::incrementAndGet));
            }
            for (Future<?> f : futures) {
                f.get(2, TimeUnit.SECONDS);
            }
            assertEquals(5, counter.get());
        }
    }

    @Test
    void rejectedExecutionWhenQueueFull() throws Exception {
        CountDownLatch workerStarted = new CountDownLatch(1);
        CountDownLatch releaseWorker = new CountDownLatch(1);
        try (ThreadPoolExecutor pool = new ThreadPoolExecutor(
                1, 1,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>())) {
            pool.execute(() -> {
                workerStarted.countDown();
                awaitQuietly(releaseWorker);
            });
            assertTrue(workerStarted.await(2, TimeUnit.SECONDS));
            assertThrows(RejectedExecutionException.class,
                    () -> pool.execute(() -> {
                    }));
        } finally {
            releaseWorker.countDown();
        }
    }

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
