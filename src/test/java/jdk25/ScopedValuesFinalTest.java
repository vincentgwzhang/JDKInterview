package jdk25;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScopedValuesFinalTest {

    private static final ScopedValue<String> TRACE = ScopedValue.newInstance();

    @Test
    void traceIdPropagatesInScope() {
        ScopedValue.where(TRACE, "trace-25").run(() ->
                assertEquals("trace-25", TRACE.get()));
    }
}
