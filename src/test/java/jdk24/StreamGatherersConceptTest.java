package jdk24;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JDK 24 Stream Gatherers 已成为标准 API；此处用等价的「窗口」逻辑演示面试常问的语义。
 * 在 JDK 24+ 可替换为 {@code Gatherers.windowFixed(2)} 等。
 */
class StreamGatherersConceptTest {

    static List<List<Integer>> fixedWindows(List<Integer> source, int size) {
        List<List<Integer>> windows = new ArrayList<>();
        for (int i = 0; i <= source.size() - size; i++) {
            windows.add(source.subList(i, i + size));
        }
        return windows;
    }

    @Test
    void slidingWindowConcept() {
        List<Integer> nums = List.of(1, 2, 3, 4);
        List<List<Integer>> pairs = fixedWindows(nums, 2);
        assertEquals(List.of(List.of(1, 2), List.of(2, 3), List.of(3, 4)), pairs);
    }

    @Test
    void gathererVsCollectorAnalogy() {
        // Collector：终端聚合；Gatherer：中间态变换（概念对比）
        int sum = List.of(1, 2, 3).stream().collect(Collectors.summingInt(i -> i));
        assertEquals(6, sum);
    }
}
