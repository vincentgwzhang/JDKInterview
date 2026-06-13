package jdk18;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class Utf8DefaultCharsetTest {

    @Test
    void defaultCharsetIsUtf8() {
        assertEquals(StandardCharsets.UTF_8, Charset.defaultCharset());
    }

    @Test
    void stringBytesUseUtf8ByDefault() {
        String text = "中文";
        byte[] bytes = text.getBytes();
        assertEquals(text, new String(bytes, StandardCharsets.UTF_8));
    }
}
