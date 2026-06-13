package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AqsUtilitiesTest {

    @Test
    void countDownLatchWaitsForWorkers() throws Exception {
        int workers = 3;
        CountDownLatch latch = new CountDownLatch(workers);
        AtomicInteger finished = new AtomicInteger();

        for (int i = 0; i < workers; i++) {
            new Thread(() -> {
                latch.countDown();
            }).start();
        }
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(0, latch.getCount());
        finished.set(workers);
        assertEquals(3, finished.get());
    }

    @Test
    void cyclicBarrierPartiesMeet() throws Exception {
        // await(timeout) 可能抛 TimeoutException
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties);
        CountDownLatch allPassed = new CountDownLatch(1);
        AtomicInteger phase = new AtomicInteger();

        for (int i = 0; i < parties; i++) {
            new Thread(() -> {
                try {
                    barrier.await(2, TimeUnit.SECONDS);
                    if (phase.incrementAndGet() == parties) {
                        allPassed.countDown();
                    }
                } catch (InterruptedException | BrokenBarrierException
                         | java.util.concurrent.TimeoutException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        assertTrue(allPassed.await(3, TimeUnit.SECONDS));
    }

    @Test
    void semaphoreLimitsConcurrency() throws Exception {
        int permits = 2;
        Semaphore semaphore = new Semaphore(permits);
        AtomicInteger concurrent = new AtomicInteger();
        AtomicInteger maxSeen = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(4);

        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    int c = concurrent.incrementAndGet();
                    maxSeen.updateAndGet(prev -> Math.max(prev, c));
                    Thread.sleep(30);
                    concurrent.decrementAndGet();
                    semaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertTrue(maxSeen.get() <= permits);
    }
}
