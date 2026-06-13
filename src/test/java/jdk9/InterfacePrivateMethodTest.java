package jdk9;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterfacePrivateMethodTest {

    interface Parser {
        default int parse(String s) {
            return parseDigits(normalize(s));
        }

        private String normalize(String s) {
            return s == null ? "" : s.strip();
        }

        private int parseDigits(String s) {
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        }
    }

    @Test
    void defaultMethodUsesPrivateHelpers() {
        Parser parser = new Parser() {};
        assertEquals(42, parser.parse("  42 "));
    }
}
