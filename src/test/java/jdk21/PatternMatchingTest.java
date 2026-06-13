package jdk21;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternMatchingTest {

    sealed interface Animal permits Dog, Cat {}
    record Dog(String name) implements Animal {}
    record Cat(int lives) implements Animal {}

    static String describe(Animal a) {
        return switch (a) {
            case Dog d when d.name().length() > 3 -> "Big dog " + d.name();
            case Dog d -> "Dog " + d.name();
            case Cat c -> "Cat lives=" + c.lives();
        };
    }

    @Test
    void switchWithGuardAndRecordPatterns() {
        assertEquals("Dog Rex", describe(new Dog("Rex")));
        assertTrue(describe(new Dog("Alexander")).startsWith("Big"));
        assertEquals("Cat lives=9", describe(new Cat(9)));
    }

    @Test
    void recordPatternInInstanceof() {
        Object obj = new Point(3, 4);
        if (obj instanceof Point(int x, int y)) {
            assertEquals(5.0, Math.hypot(x, y), 1e-9);
        } else {
            fail("expected Point");
        }
    }

    record Point(int x, int y) {}
}
