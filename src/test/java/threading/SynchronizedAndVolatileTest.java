package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedAndVolatileTest {

    static class Counter {
        private int count;
        private volatile boolean shutdown;

        synchronized void increment() {
            count++;
        }

        int getCount() {
            return count;
        }

        void shutdown() {
            shutdown = true;
        }

        boolean isShutdown() {
            return shutdown;
        }
    }

    @Test
    void synchronizedMakesIncrementAtomic() throws Exception {
        Counter counter = new Counter();
        int threads = 8;
        int perThread = 1000;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < perThread; j++) {
                        counter.increment();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertEquals(threads * perThread, counter.getCount());
    }

    @Test
    void volatilePublishesFlag() throws Exception {
        Counter counter = new Counter();
        CountDownLatch seen = new CountDownLatch(1);
        Thread reader = new Thread(() -> {
            while (!counter.isShutdown()) {
                Thread.onSpinWait();
            }
            seen.countDown();
        });
        reader.start();
        Thread.sleep(20);
        counter.shutdown();
        assertTrue(seen.await(2, TimeUnit.SECONDS));
        reader.join(1000);
    }
}
