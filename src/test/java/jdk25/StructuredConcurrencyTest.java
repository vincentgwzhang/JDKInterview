package jdk25;

import org.junit.jupiter.api.Test;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class StructuredConcurrencyTest {

    @Test
    void structuredTaskScopeJoinsSubtasks() throws Exception {
        try (var scope = StructuredTaskScope.<String>open()) {
            Subtask<String> a = scope.fork(() -> "A");
            Subtask<String> b = scope.fork(() -> "B");
            scope.join();
            assertEquals("AB", a.get() + b.get());
        }
    }
}
