package jdk15;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringApiTest {

    /*
     * stripIndent() 面试点：
     *
     * 1. 它是 JDK 15 的 String API，不属于 JDK 12。
     * 2. 主要目的：移除多行字符串的“公共缩进”。
     * 3. 它不是把每一行左侧空格全部删光；它只删除所有非空行共同拥有的最小缩进。
     */
    @Test
    void stripIndentRemovesCommonIndent() {
        String text = "  a\n    b\n  c";

        assertEquals("a\n  b\nc", text.stripIndent());
    }

    @Test
    void stripIndentDoesNotRemoveAllLeadingSpaces() {
        String text = "  a\n                       b\n        c";

        assertEquals("a\n                     b\n      c", text.stripIndent());
    }
}
