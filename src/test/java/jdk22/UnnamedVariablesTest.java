package jdk22;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UnnamedVariablesTest {

    @Test
    void unnamedInForAndTry() {
        List<String> items = List.of("a", "b");
        int count = 0;
        for (var _ : items) {
            count++;
        }
        assertEquals(2, count);

        Map<String, Integer> map = Map.of("k", 1);
        try {
            map.put("x", 2);
        } catch (UnsupportedOperationException _) {
            assertTrue(true);
        }
    }
}
