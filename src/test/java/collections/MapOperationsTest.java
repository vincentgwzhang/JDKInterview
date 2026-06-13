package collections;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MapOperationsTest {

    @Test
    void hashMapPutGetAndOverwrite() {
        Map<String, Integer> map = new HashMap<>();
        map.put("java", 8);
        map.put("java", 11);
        assertEquals(11, map.get("java"));
        assertEquals(1, map.size());
        assertEquals(99, map.getOrDefault("go", 99));
    }

    @Test
    void linkedHashMapAccessOrder() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>(16, 0.75f, true);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.get("a");
        assertEquals(List.of("b", "c", "a"), List.copyOf(map.keySet()));
    }

    @Test
    void treeMapSortedByKey() {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("banana", "B");
        map.put("apple", "A");
        assertEquals("apple", map.firstKey());
        assertEquals(List.of("A", "B"), List.copyOf(map.values()));
    }

    @Test
    void computeIfAbsentBuildsCacheEntryOnce() {
        Map<String, AtomicInteger> cache = new HashMap<>();
        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < 3; i++) {
            cache.computeIfAbsent("key", k -> {
                counter.incrementAndGet();
                return new AtomicInteger();
            }).incrementAndGet();
        }
        assertEquals(1, counter.get());
        assertEquals(3, cache.get("key").get());
    }

    @Test
    void mergeCombinesValues() {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("team", 10);
        scores.merge("team", 5, Integer::sum);
        scores.merge("solo", 7, Integer::sum);
        assertEquals(15, scores.get("team"));
        assertEquals(7, scores.get("solo"));
    }
}
