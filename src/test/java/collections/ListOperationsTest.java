package collections;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

class ListOperationsTest {

    @Test
    void arrayListRandomAccessAndGrowth() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        assertEquals("a", list.get(0));
        list.add(1, "x");
        assertEquals(List.of("a", "x", "b"), list);
    }

    @Test
    void linkedListDequeOperations() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addFirst(1);
        list.addLast(2);
        list.addFirst(0);
        assertEquals(List.of(0, 1, 2), list);
        assertEquals(0, list.pollFirst());
        assertEquals(2, list.pollLast());
    }

    @Test
    void subListIsViewOnBackingList() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4));
        List<Integer> sub = list.subList(1, 3);
        assertEquals(List.of(2, 3), sub);
        list.set(2, 99);
        assertEquals(List.of(2, 99), sub);
    }

    @Test
    void listIteratorCanIterateBackwards() {
        List<String> list = List.of("a", "b", "c");
        ListIterator<String> it = list.listIterator(list.size());
        List<String> reversed = new ArrayList<>();
        while (it.hasPrevious()) {
            reversed.add(it.previous());
        }
        assertEquals(List.of("c", "b", "a"), reversed);
    }

    @Test
    void arraysAsListFixedSize() {
        List<String> list = Arrays.asList("x", "y");
        assertEquals(2, list.size());
        assertThrows(UnsupportedOperationException.class, () -> list.add("z"));
        list.set(0, "X");
        assertEquals("X", list.get(0));
    }

    /**
     * 发现修改立即抛异常
     *
     * 创建迭代器时：expectedModCount = modCount = 3
     * 调用 list.remove() 后：modCount 变为 4
     * 下次调用 next() 时：expectedModCount(3) ≠ modCount(4) → 抛异常
     */
    @Test
    void failFastExample() {
        List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));
        assertThrows(ConcurrentModificationException.class, () -> {
            for (String s : list) {
                if (s.equals("B")) {
                    list.remove(s);
                }
            }
        });
    }

    @Test
    void failSafeExample() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(List.of("A", "B", "C", "D"));
        for (String s : list) {
            if (s.equals("B")) {
                list.remove(s);  // 不抛异常，但迭代的仍是旧快照
            }
        }
        assertEquals(List.of("A", "C", "D"), list);
    }
}
