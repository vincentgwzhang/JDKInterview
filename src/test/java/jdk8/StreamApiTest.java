package jdk8;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StreamApiTest {

    @Test
    void lazyIntermediateOps() {
        Stream<String> stream = Stream.of("a", "bb", "ccc")
                .filter(s -> {
                    // 只有终端操作执行时才会跑
                    return s.length() > 1;
                });
        List<String> result = stream.collect(Collectors.toList());
        assertEquals(List.of("bb", "ccc"), result);
    }

    @Test
    void mapFlatMapReduce() {
        List<String> words = List.of("Java Stream", "JDK 8");
        int totalChars = words.stream()
                .flatMap(s -> Stream.of(s.split("\\s+")))
                .mapToInt(String::length)
                .sum();
        assertEquals(4 + 6 + 3 + 1, totalChars); // Java, Stream, JDK, 8
    }

    @Test
    void parallelStreamConcept() {
        List<Integer> nums = List.of(1, 2, 3, 4);
        int sum = nums.parallelStream().mapToInt(i -> i * i).sum();
        assertEquals(30, sum);
    }
}
