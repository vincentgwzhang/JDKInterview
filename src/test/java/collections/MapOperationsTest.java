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

    /*
     * computeIfAbsent 面试点：
     *
     * 1. 主要目的：懒加载 cache entry。
     *    如果 key 不存在，才执行 mappingFunction 创建 value，并放入 map；
     *    如果 key 已经存在，直接返回已有 value，不会再次执行 mappingFunction。
     *
     * 2. 这个测试循环 3 次都访问同一个 key：
     *    - 第 1 次：key 不存在，counter +1，创建新的 AtomicInteger 放入 cache。
     *    - 第 2、3 次：key 已存在，直接拿旧的 AtomicInteger，counter 不再增加。
     *
     * 3. 所以最后 counter == 1，说明缓存对象只构建了一次；
     *    cache.get("key").get() == 3，说明三次都对同一个缓存对象做了 increment。
     *
     * 4. 常见用途：
     *    Map<String, List<Item>> 分组时，按需创建 List；
     *    Map<Key, ExpensiveObject> 缓存时，按需创建昂贵对象。
     *
     * 5. 注意：HashMap 的 computeIfAbsent 本身不是线程安全的。
     *    多线程缓存场景通常用 ConcurrentHashMap.computeIfAbsent。
     */
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

    /*
     * merge 面试点：
     *
     * 1. 主要目的：把“如果 key 不存在就插入初始值，如果 key 已存在就合并旧值和新值”
     *    这套逻辑合成一个 API。
     *
     * 2. merge(key, value, remappingFunction) 的语义：
     *    - key 不存在，或当前 value 为 null：直接把传入的 value 放进去。
     *    - key 已存在且当前 value 非 null：执行 remappingFunction(oldValue, value)，把结果放回去。
     *
     * 3. 这个测试里：
     *    - scores 原来有 team=10，所以 merge("team", 5, Integer::sum) 得到 10 + 5 = 15。
     *    - scores 原来没有 solo，所以 merge("solo", 7, Integer::sum) 直接插入 solo=7，
     *      不需要执行真正的合并。
     *
     * 4. 常见用途：
     *    计数、累加、统计分数、拼接字符串、合并集合。
     *
     * 5. 注意：如果 remappingFunction 返回 null，Map 会删除这个 key。
     *    这是 merge 很容易被问到的细节。
     */
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
