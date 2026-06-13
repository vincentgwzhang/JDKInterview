package jdk13;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextBlocksTest {

    @Test
    void multiLineJson() {
        String json = """
                {
                  "name": "Java",
                  "version": 13
                }
                """;
        assertTrue(json.contains("\"version\": 13"));
        assertTrue(json.contains("\n"));
    }

    @Test
    void escapeLineContinuation() {
        String oneLine = """
                hello \
                world
                """;
        assertTrue(oneLine.contains("hello world"));
    }
}
