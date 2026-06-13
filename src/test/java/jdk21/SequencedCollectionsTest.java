package jdk21;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SequencedCollectionsTest {

    @Test
    void listFirstLastAndReversed() {
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        assertEquals("a", list.getFirst());
        assertEquals("c", list.getLast());
        list.addFirst("z");
        assertEquals("z", list.getFirst());
        assertEquals(List.of("z", "a", "b", "c"), list);
        assertEquals(List.of("c", "b", "a", "z"), list.reversed());
    }

    @Test
    void linkedHashMapSequenced() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals("one", map.firstEntry().getKey());
        assertEquals("two", map.lastEntry().getKey());
        map.putFirst("zero", 0);
        assertEquals("zero", map.firstEntry().getKey());
    }
}
