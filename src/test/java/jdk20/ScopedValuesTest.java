package jdk20;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScopedValuesTest {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    @Test
    void scopedValueBoundInScope() {
        ScopedValue.where(REQUEST_ID, "req-20").run(() -> {
            assertEquals("req-20", REQUEST_ID.get());
            ScopedValue.where(REQUEST_ID, "nested").run(() ->
                    assertEquals("nested", REQUEST_ID.get()));
            assertEquals("req-20", REQUEST_ID.get());
        });
        assertFalse(REQUEST_ID.isBound());
    }
}
