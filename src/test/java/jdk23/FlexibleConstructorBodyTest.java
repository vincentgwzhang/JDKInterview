package jdk23;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlexibleConstructorBodyTest {

    static class Base {
        final int value;

        Base(int value) {
            this.value = value;
        }
    }

    /** JDK 23+：在 super(...) 之前计算参数 */
    static class Derived extends Base {
        Derived(int input) {
            int doubled = input * 2;
            super(doubled);
        }
    }

    static class User {
        final String name;

        User(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("blank name");
            }
            this.name = name.strip();
        }
    }

    @Test
    void statementsBeforeSuperCall() {
        assertEquals(20, new Derived(10).value);
    }

    @Test
    void validationInConstructor() {
        User u = new User("  Alice  ");
        assertEquals("Alice", u.name);
        assertThrows(IllegalArgumentException.class, () -> new User("  "));
    }
}
