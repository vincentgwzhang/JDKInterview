package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 演示死锁成因与 tryLock 避免方式（不在测试中故意挂死 JUnit）。
 */
class DeadlockAwarenessTest {

    @Test
    void orderedLocksPreventDeadlock() throws Exception {
        Lock lockA = new ReentrantLock();
        Lock lockB = new ReentrantLock();
        var ok = new java.util.concurrent.atomic.AtomicBoolean(false);

        Thread t1 = new Thread(() -> acquireOrdered(lockA, lockB, () -> ok.set(true)));
        Thread t2 = new Thread(() -> acquireOrdered(lockA, lockB, () -> ok.set(true)));
        t1.start();
        t2.start();
        t1.join(2000);
        t2.join(2000);
        assertTrue(ok.get());
    }

    @Test
    void tryLockFailsInsteadOfDeadlocking() throws Exception {
        Lock lockA = new ReentrantLock();
        CountDownLatch aHeld = new CountDownLatch(1);
        Thread holder = new Thread(() -> {
            lockA.lock();
            try {
                aHeld.countDown();
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lockA.unlock();
            }
        });
        holder.start();
        assertTrue(aHeld.await(2, TimeUnit.SECONDS));
        assertFalse(lockA.tryLock(50, TimeUnit.MILLISECONDS));
        holder.join(2000);
    }

    private static void acquireOrdered(Lock first, Lock second, Runnable action) {
        first.lock();
        try {
            second.lock();
            try {
                action.run();
            } finally {
                second.unlock();
            }
        } finally {
            first.unlock();
        }
    }
}
