package jdk9;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableCollectionsTest {

    @Test
    void listOfIsImmutable() {
        List<String> list = List.of("a", "b");
        assertEquals(2, list.size());
        assertThrows(UnsupportedOperationException.class, () -> list.add("c"));
    }

    @Test
    void mapOfRejectsNullAndDuplicateKeys() {
        Map<String, Integer> m = Map.of("k1", 1, "k2", 2);
        assertEquals(1, m.get("k1"));
        assertThrows(NullPointerException.class, () -> Map.of("a", null));
        assertThrows(IllegalArgumentException.class, () -> Map.of("x", 1, "x", 2));
    }

    @Test
    void setOf() {
        Set<Integer> s = Set.of(1, 2, 3);
        assertTrue(s.contains(2));
    }
}
