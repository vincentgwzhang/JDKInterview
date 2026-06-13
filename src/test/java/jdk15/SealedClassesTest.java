package jdk15;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SealedClassesTest {

    sealed interface Shape permits Circle, Rectangle {}

    record Circle(double radius) implements Shape {}

    record Rectangle(double w, double h) implements Shape {}

    static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.w() * r.h();
        };
    }

    @Test
    void sealedEnablesExhaustiveSwitch() {
        assertEquals(Math.PI, area(new Circle(1)), 1e-9);
        assertEquals(6, area(new Rectangle(2, 3)));
    }
}
