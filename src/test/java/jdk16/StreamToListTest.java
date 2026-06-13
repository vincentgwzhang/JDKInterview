package jdk16;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class StreamToListTest {

    @Test
    void toListReturnsUnmodifiableList() {
        List<String> immutable = List.of("a", "b").stream().toList();
        assertEquals(2, immutable.size());
        assertThrows(UnsupportedOperationException.class, () -> immutable.add("c"));
    }

    @Test
    void collectToListIsMutable() {
        List<String> mutable = List.of("a").stream()
                .collect(Collectors.toList());
        mutable.add("b");
        assertEquals(2, mutable.size());
    }
}
