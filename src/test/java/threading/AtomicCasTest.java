package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

import static org.junit.jupiter.api.Assertions.*;

class AtomicCasTest {

    @Test
    void atomicIncrementUnderContention() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        int threads = 10;
        int perThread = 1000;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < perThread; j++) {
                        counter.incrementAndGet();
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
        assertEquals(threads * perThread, counter.get());
    }

    @Test
    void compareAndSet() {
        AtomicInteger ai = new AtomicInteger(10);
        assertTrue(ai.compareAndSet(10, 20));
        assertFalse(ai.compareAndSet(10, 30));
        assertEquals(20, ai.get());
    }

    @Test
    void stampedReferenceDetectsAba() {
        AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
        int[] stamp = new int[1];
        String val = ref.get(stamp);
        assertEquals("A", val);
        assertTrue(ref.compareAndSet("A", "B", stamp[0], stamp[0] + 1));
        assertFalse(ref.compareAndSet("A", "C", 0, 1));
    }
}
