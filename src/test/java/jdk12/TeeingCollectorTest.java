package jdk12;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TeeingCollectorTest {

    record MinMax(int min, int max) {}

    @Test
    void teeingCombinesTwoCollectors() {
        List<Integer> nums = List.of(3, 1, 4, 1, 5);
        MinMax mm = nums.stream().collect(Collectors.teeing(
                Collectors.minBy(Integer::compareTo),
                Collectors.maxBy(Integer::compareTo),
                (min, max) -> new MinMax(min.orElseThrow(), max.orElseThrow())
        ));
        assertEquals(1, mm.min());
        assertEquals(5, mm.max());
    }
}
