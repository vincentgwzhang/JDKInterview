package jdk11;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class StringApiTest {

    @Test
    void isBlankAndStrip() {
        assertTrue("  \t".isBlank());
        assertEquals("Java", "  Java  ".strip());
        assertEquals("Java", "  Java".stripLeading());
    }

    @Test
    void linesAndRepeat() {
        String multi = "a\nb\nc";
        assertEquals("a|b|c", multi.lines().collect(Collectors.joining("|")));
        assertEquals("ha", "ha".repeat(1));
        assertEquals("hahaha", "ha".repeat(3));
    }
}
