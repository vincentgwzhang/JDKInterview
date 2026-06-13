package jdk21;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class VirtualThreadsTest {

    @Test
    void virtualThreadPerTaskExecutor() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future = executor.submit(() -> Thread.currentThread().isVirtual());
            assertTrue(future.get(2, TimeUnit.SECONDS));
        }
    }
}
