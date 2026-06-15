package collections;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FailFastAndImmutableTest {

    @Test
    void failFastOnStructuralChangeDuringIteration() {
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        Iterator<String> it = list.iterator();
        assertEquals("a", it.next());
        list.add("d");
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    void listOfIsTrulyImmutable() {
        List<String> list = List.of("x", "y");
        assertThrows(UnsupportedOperationException.class, () -> list.set(0, "z"));
        assertThrows(UnsupportedOperationException.class, () -> list.add("z"));
    }

    @Test
    void unmodifiableViewReflectsBackingList() {
        List<String> backing = new ArrayList<>(List.of("a"));
        List<String> view = List.copyOf(backing);
        backing.add("b");
        assertEquals(1, view.size());
        assertEquals(List.of("a"), view);
    }

    /**
     * 
     * Map.of 函数是不接受 null key 和 null value 的
     * 
     */
    @Test
    void mapOfRejectsNullKey() {
        assertThrows(NullPointerException.class, () -> Map.of((String) null, 1));
    }

    /**
     * 但跟 HashMap 接受 null key 和 null value 无关
     * 
     */
    @Test
    void mapInit() {
        Map<String, String> maps = new HashMap<>();
        maps.put(null, null);
    }
}
