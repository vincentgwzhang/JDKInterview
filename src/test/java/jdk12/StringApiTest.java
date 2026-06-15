package jdk12;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringApiTest {

    /*
     * indent(int n) 面试点：
     *
     * 1. 主要目的：按“行”调整缩进。
     *    n > 0：每一行前面加 n 个空格。
     *    n < 0：每一行最多删除 n 的绝对值个前导空白。
     *
     * 2. indent 会规范化换行，结果会以换行符结尾。
     */
    @Test
    void indentAddsSpacesToEachLine() {
        String text = "a\nb";

        assertEquals("  a\n  b\n", text.indent(2));
    }

    @Test
    void indentCanRemoveLeadingSpaces() {
        String text = "    a\n  b\nc";

        assertEquals("  a\nb\nc\n", text.indent(-2));
    }

    /*
     * transform(Function<String, R>) 面试点：
     *
     * 1. 主要目的：把当前字符串交给一个函数处理，让字符串处理可以继续链式表达。
     * 2. 返回值类型是 R，不一定是 String。
     */
    @Test
    void transformCanReturnAnotherString() {
        String result = "java"
                .transform(String::toUpperCase)
                .indent(2);

        assertEquals("  JAVA\n", result);
    }

    @Test
    void transformCanReturnNonStringValue() {
        int length = "jdk12".transform(String::length);

        assertEquals(5, length);
    }
}
