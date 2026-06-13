package jdk14;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecordsTest {

    record Point(int x, int y) {
        Point {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("negative");
            }
        }
    }

    @Test
    void recordCompactConstructorAndAccessors() {
        Point p = new Point(1, 2);
        assertEquals(1, p.x());
        assertEquals(new Point(1, 2), p);
    }

    @Test
    void recordRejectsInvalidState() {
        assertThrows(IllegalArgumentException.class, () -> new Point(-1, 0));
    }
}
