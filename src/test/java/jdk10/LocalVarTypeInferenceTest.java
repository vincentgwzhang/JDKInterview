package jdk10;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalVarTypeInferenceTest {

    @Test
    void varInfersConcreteType() {
        var names = List.of("Java", "10");
        assertEquals(2, names.size());
        assertInstanceOf(List.class, names);
    }

    @Test
    void varWithExplicitGenericOnRhs() {
        var map = new HashMap<String, Integer>();
        map.put("jdk", 10);
        assertEquals(10, map.get("jdk"));
    }

    @Test
    void varInForLoop() {
        var list = new ArrayList<>(List.of(1, 2, 3));
        var sum = 0;
        for (var n : list) {
            sum += n;
        }
        assertEquals(6, sum);
    }

    @Test
    void varCannotReassignIncompatibleType() {
        var list = List.of(1);
        // list = List.of("a"); // 编译错误
        assertEquals(1, list.get(0));
    }
}
