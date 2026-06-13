package jdk19;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class VirtualThreadsTest {

    @Test
    void manyVirtualThreadsComplete() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            AtomicInteger counter = new AtomicInteger();
            for (int i = 0; i < 10_000; i++) {
                executor.submit(() -> counter.incrementAndGet());
            }
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
            assertEquals(10_000, counter.get());
        }
    }

    @Test
    void startVirtualThread() throws Exception {
        var ref = new java.util.concurrent.atomic.AtomicReference<String>();
        Thread t = Thread.startVirtualThread(() -> ref.set(Thread.currentThread().isVirtual() ? "virtual" : "platform"));
        t.join();
        assertEquals("virtual", ref.get());
    }
}
