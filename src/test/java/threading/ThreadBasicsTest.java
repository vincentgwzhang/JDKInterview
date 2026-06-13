package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ThreadBasicsTest {

    @Test
    void startCreatesNewThread() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Thread t = new Thread(() -> started.countDown());
        assertEquals(Thread.State.NEW, t.getState());
        t.start();
        assertTrue(started.await(2, TimeUnit.SECONDS));
        t.join(2000);
        assertFalse(t.isAlive());
        assertEquals(Thread.State.TERMINATED, t.getState());
    }

    @Test
    void interruptStopsBlockingSleep() throws Exception {
        AtomicBoolean interrupted = new AtomicBoolean();
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                interrupted.set(true);
                Thread.currentThread().interrupt();
            }
        });
        t.start();
        Thread.sleep(50);
        t.interrupt();
        t.join(2000);
        assertTrue(interrupted.get());
        assertTrue(t.isInterrupted());
    }

    @Test
    void callableViaFutureTask() throws Exception {
        var task = new java.util.concurrent.FutureTask<>(() -> 42);
        Thread t = new Thread(task);
        t.start();
        assertEquals(42, task.get(2, TimeUnit.SECONDS));
    }
}
