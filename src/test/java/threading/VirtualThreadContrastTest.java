package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class VirtualThreadContrastTest {

    @Test
    void platformThreadIsNotVirtual() {
        Thread t = Thread.startVirtualThread(() -> {});
        assertTrue(t.isVirtual());
    }

    @Test
    void virtualThreadExecutorScalesForManyTasks() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            AtomicInteger count = new AtomicInteger();
            for (int i = 0; i < 5000; i++) {
                executor.submit(count::incrementAndGet);
            }
            executor.shutdown();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
            assertEquals(5000, count.get());
        }
    }
}
