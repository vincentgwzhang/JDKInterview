package jdk17;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SealedAndSwitchPatternTest {

    sealed interface Expr permits Constant, Neg {}

    record Constant(int value) implements Expr {}

    record Neg(Expr inner) implements Expr {}

    static int eval(Expr e) {
        return switch (e) {
            case Constant c -> c.value();
            case Neg n -> -eval(n.inner());
        };
    }

    @Test
    void switchPatternOnSealedHierarchy() {
        assertEquals(5, eval(new Constant(5)));
        assertEquals(-3, eval(new Neg(new Constant(3))));
    }
}
