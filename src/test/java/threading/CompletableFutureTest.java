package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CompletableFutureTest {

    @Test
    void thenApplyChainsResult() throws Exception {
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> "hello")
                .thenApply(s -> s + " world");
        assertEquals("hello world", future.get(2, TimeUnit.SECONDS));
    }

    @Test
    void thenComposeFlattensNestedFuture() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> 2)
                .thenCompose(n -> CompletableFuture.supplyAsync(() -> n * n));
        assertEquals(4, future.get(2, TimeUnit.SECONDS));
    }

    @Test
    void allOfWaitsForAll() throws Exception {
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> "A");
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> "B");
        CompletableFuture<Void> all = CompletableFuture.allOf(a, b);
        all.get(2, TimeUnit.SECONDS);
        assertEquals("AB", a.get() + b.get());
    }

    @Test
    void exceptionallyHandlesFailure() {
        CompletableFuture<Integer> future = CompletableFuture
                .<Integer>supplyAsync(() -> {
                    throw new IllegalStateException("boom");
                })
                .exceptionally(ex -> -1);
        assertEquals(-1, future.join());
    }

    @Test
    void customExecutor() throws Exception {
        try (var pool = Executors.newFixedThreadPool(1)) {
            String name = CompletableFuture
                    .supplyAsync(() -> Thread.currentThread().getName(), pool)
                    .get(2, TimeUnit.SECONDS);
            assertTrue(name.startsWith("pool"));
        }
    }
}
