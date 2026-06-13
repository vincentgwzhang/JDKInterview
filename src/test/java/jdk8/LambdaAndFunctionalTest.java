package jdk8;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class LambdaAndFunctionalTest {

    @Test
    void lambdaAndMethodReference() {
        Predicate<String> notBlank = s -> s != null && !s.trim().isEmpty();
        assertTrue(notBlank.test("hi"));

        Function<String, Integer> length = String::length;
        assertEquals(3, length.apply("abc"));

        Comparator<String> byLen = Comparator.comparingInt(String::length);
        assertTrue(byLen.compare("a", "abc") < 0);
    }

    @Test
    void effectivelyFinalCapture() {
        String prefix = "JDK";
        Supplier<String> supplier = () -> prefix + "8";
        assertEquals("JDK8", supplier.get());
        // prefix = "x"; // 编译错误：非 effectively final
    }
}
