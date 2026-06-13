package jdk14;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternMatchingInstanceofTest {

    static int lengthOf(Object obj) {
        if (obj instanceof String s) {
            return s.length();
        }
        return -1;
    }

    @Test
    void patternMatchingAvoidsCast() {
        assertEquals(4, lengthOf("Java"));
        assertEquals(-1, lengthOf(42));
    }
}
