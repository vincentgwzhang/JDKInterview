package jdk13;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwitchYieldReturnTest {

    /*
     * yield vs return 面试点：
     *
     * 1. yield 只用于 switch 表达式内部，作用是“给这个 switch 表达式产出一个值”。
     *    yield 结束的是当前 switch 分支，不会结束外围方法。
     *
     * 2. return 是方法级别的控制流，作用是“直接结束整个方法并返回”。
     *
     * 3. 所以：
     *    - yield 后，switch 表达式结束，方法后面的代码还会继续执行。
     *    - return 后，整个方法结束，方法后面的代码不会执行。
     */
    @Test
    void yieldOnlyProducesSwitchExpressionValue() {
        assertEquals("switch value=A, method continued", describeWithYield(1));
        assertEquals("switch value=other, method continued", describeWithYield(9));
    }

    @Test
    void returnExitsTheWholeMethod() {
        assertEquals("returned from case 1", describeWithReturn(1));
        assertEquals("method continued after switch", describeWithReturn(9));
    }

    private static String describeWithYield(int code) {
        String value = switch (code) {
            case 1 -> {
                yield "A";
            }
            default -> {
                yield "other";
            }
        };

        return "switch value=" + value + ", method continued";
    }

    private static String describeWithReturn(int code) {
        switch (code) {
            case 1 -> {
                return "returned from case 1";
            }
            default -> {
                // The switch statement ends, then the method continues below.
            }
        }

        return "method continued after switch";
    }
}
