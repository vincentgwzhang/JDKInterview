package jdk8;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class OptionalAndInterfaceTest {

    interface Greeter {
        default String greet(String name) {
            return "Hello, " + name;
        }

        static String version() {
            return "JDK8";
        }
    }

    static class EnglishGreeter implements Greeter {
        @Override
        public String greet(String name) {
            return Greeter.super.greet(name) + " (en)";
        }
    }

    // 继承是可以的
    static class FranceGreeter extends EnglishGreeter {
        @Override
        public String greet(String name) {
            return super.greet(name);
        }
    }

    @Test
    void optionalOrElseVsOrElseGet() {
        Optional<String> empty = Optional.empty();
        AtomicInteger calls = new AtomicInteger();
        assertEquals("default", empty.orElse("default"));
        assertEquals("lazy", empty.orElseGet(() -> {
            calls.incrementAndGet();
            return "lazy";
        }));
        assertEquals(1, calls.get());
    }

    @Test
    void defaultAndStaticOnInterface() {
        assertEquals("Hello, Java (en)", new EnglishGreeter().greet("Java"));
        assertEquals("JDK8", Greeter.version());
    }

    @Test
    void javaTimeApi() {
        LocalDate d = LocalDate.of(2014, 3, 18);
        assertEquals(2014, d.getYear());
        assertTrue(d.isBefore(LocalDate.now()));
    }
}
