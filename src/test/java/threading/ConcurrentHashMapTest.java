package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentHashMapTest {

    @Test
    void computeIfAbsentOnlyOncePerKey() throws Exception {
        ConcurrentHashMap<String, AtomicInteger> cache = new ConcurrentHashMap<>();
        int threads = 8;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger factoryCalls = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    cache.computeIfAbsent("key", k -> {
                        factoryCalls.incrementAndGet();
                        return new AtomicInteger();
                    }).incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        assertTrue(done.await(3, TimeUnit.SECONDS));
        assertEquals(1, factoryCalls.get());
        assertEquals(threads, cache.get("key").get());
    }

    @Test
    void putIfAbsent() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        assertNull(map.putIfAbsent("a", "1"));
        assertEquals("1", map.putIfAbsent("a", "2"));
        assertEquals("1", map.get("a"));
    }
}
