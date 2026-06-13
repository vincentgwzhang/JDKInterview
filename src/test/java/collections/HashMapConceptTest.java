package collections;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 演示 HashMap 与 HashSet 关系、hash 桶行为（面试口述 JDK8 数组+链表/树）。
 */
class HashMapConceptTest {

    @Test
    void hashSetBackedByHashMap() {
        var set = new java.util.HashSet<>(java.util.List.of("a", "b"));
        assertEquals(2, set.size());
        // HashSet 内部 HashMap 的 value 为固定 Object PRESENT
    }

    @Test
    void equalKeysMustShareHashCode() {
        Map<Key, String> map = new HashMap<>();
        Key k1 = new Key("id", 1);
        Key k2 = new Key("id", 1);
        map.put(k1, "v1");
        assertEquals("v1", map.get(k2));
        assertEquals(1, map.size());
    }

    @Test
    void manyInsertsTriggerInternalResize() {
        HashMap<Integer, String> map = new HashMap<>(4);
        for (int i = 0; i < 20; i++) {
            map.put(i, "v" + i);
        }
        assertEquals(20, map.size());
        for (int i = 0; i < 20; i++) {
            assertEquals("v" + i, map.get(i));
        }
    }

    record Key(String id, int version) {
        @Override
        public int hashCode() {
            return id.hashCode() ^ version;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Key k && id.equals(k.id) && version == k.version;
        }
    }
}
