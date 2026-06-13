package collections;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class SetOperationsTest {

    record Person(String name, int age) {}

    @Test
    void hashSetDeduplicatesByEqualsAndHashCode() {
        Set<Person> set = new HashSet<>();
        set.add(new Person("Alice", 20));
        set.add(new Person("Alice", 20));
        set.add(new Person("Bob", 22));
        assertEquals(2, set.size());
        assertTrue(set.contains(new Person("Alice", 20)));
    }

    @Test
    void linkedHashSetKeepsInsertionOrder() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("c");
        set.add("a");
        set.add("b");
        assertEquals(List.of("c", "a", "b"), List.copyOf(set));
    }

    @Test
    void treeSetSortsKeys() {
        TreeSet<Integer> set = new TreeSet<>();
        set.add(3);
        set.add(1);
        set.add(2);
        assertEquals(List.of(1, 2, 3), List.copyOf(set));
        assertEquals(1, set.first());
        assertEquals(3, set.last());
    }
}
