package jdk12;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwitchExpressionTest {

    @Test
    void switchExpressionWithArrow() {
        int day = 3;
        String type = switch (day) {
            case 1, 2, 3, 4, 5 -> "工作日";
            case 6, 7 -> "周末";
            default -> "未知";
        };
        assertEquals("工作日", type);
    }

    @Test
    void switchExpressionYieldStyle() {
        int score = 85;
        String grade = switch (score / 10) {
            case 10, 9 -> "A";
            case 8 -> "B";
            default -> {
                yield "C";
            }
        };
        assertEquals("B", grade);
    }
}
