package jdk11;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OptionalAndFilesTest {

    @Test
    void optionalIsEmpty() {
        assertTrue(Optional.empty().isEmpty());
        assertFalse(Optional.of("x").isEmpty());
    }

    @Test
    void filesReadWriteString() throws Exception {
        Path temp = Files.createTempFile("jdk11", ".txt");
        try {
            Files.writeString(temp, "hello", StandardCharsets.UTF_8);
            assertEquals("hello", Files.readString(temp, StandardCharsets.UTF_8));
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
