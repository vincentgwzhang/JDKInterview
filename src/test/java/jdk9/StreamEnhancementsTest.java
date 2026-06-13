package jdk9;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StreamEnhancementsTest {

    @Test
    void takeWhileStopsOnFirstFalse() {
        List<Integer> ordered = List.of(2, 4, 6, 7, 8);
        List<Integer> taken = ordered.stream()
                .takeWhile(n -> n % 2 == 0)
                .collect(Collectors.toList());
        assertEquals(List.of(2, 4, 6), taken);
    }

    @Test
    void dropWhileSkipsPrefix() {
        List<Integer> nums = List.of(1, 3, 5, 2, 4);
        List<Integer> rest = nums.stream()
                .dropWhile(n -> n % 2 == 1)
                .collect(Collectors.toList());
        assertEquals(List.of(2, 4), rest);
    }

    @Test
    void ofNullable() {
        String nullName = null;
        long count = Stream.ofNullable(nullName)
                .count();
        assertEquals(0, count);
        assertEquals(1, Stream.ofNullable("x").count());
    }
}
