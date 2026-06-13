package jdk9;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class TryWithResourcesTest {

    @Test
    void resourceCanBeDeclaredOutsideTry() throws IOException {
        byte[] data = "JDK9".getBytes(StandardCharsets.UTF_8);
        InputStream in = new ByteArrayInputStream(data);
        String result;
        try (in) {
            result = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertEquals("JDK9", result);
        // JDK9：effectively final 的资源变量可在 try 外声明并传入 try (in)
    }
}
