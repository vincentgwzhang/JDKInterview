package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class LockAndConditionTest {

    static class OneSlotBuffer {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull = lock.newCondition();
        private Integer slot;

        void put(int value) throws InterruptedException {
            lock.lock();
            try {
                while (slot != null) {
                    notFull.await();
                }
                slot = value;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        int take() throws InterruptedException {
            lock.lock();
            try {
                while (slot == null) {
                    notEmpty.await();
                }
                int v = slot;
                slot = null;
                notFull.signal();
                return v;
            } finally {
                lock.unlock();
            }
        }
    }

    @Test
    void reentrantLockWithTwoConditions() throws Exception {
        OneSlotBuffer buffer = new OneSlotBuffer();
        CountDownLatch ok = new CountDownLatch(1);

        Thread t = new Thread(() -> {
            try {
                buffer.put(99);
                assertEquals(99, buffer.take());
                ok.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        t.start();
        assertTrue(ok.await(2, TimeUnit.SECONDS));
    }

    @Test
    void tryLockWithTimeout() throws Exception {
        ReentrantLock lock = new ReentrantLock();
        CountDownLatch holding = new CountDownLatch(1);
        Thread owner = new Thread(() -> {
            lock.lock();
            try {
                holding.countDown();
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });
        owner.start();
        assertTrue(holding.await(2, TimeUnit.SECONDS));
        assertFalse(lock.tryLock(50, TimeUnit.MILLISECONDS));
        owner.join(2000);
        assertTrue(lock.tryLock(50, TimeUnit.MILLISECONDS));
        lock.unlock();
    }
}
