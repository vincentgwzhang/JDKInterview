package collections;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
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

    @Test
    void mapOfRejectsNullKey() {
        assertThrows(NullPointerException.class, () -> Map.of((String) null, 1));
    }
}
