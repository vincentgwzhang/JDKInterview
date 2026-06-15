package jdk9;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterfacePrivateMethodTest {

    interface Parser {
        static int defaultRadix() {
            return 10;
        }

        default int parse(String s) {
            return parseDigits(normalize(s));
        }

        /*
         * interface static 方法面试点：
         *
         * 1. static 方法属于接口本身，不属于实现类实例。
         * 2. 即使在同一个 interface 的 default 方法里，也应该用接口名调用：
         *    Parser.defaultRadix()
         * 3. 不能把它当成实例方法来理解；实现类不会“继承”这个 static 方法。
         */
        default int radix() {
            return Parser.defaultRadix();
        }

        private String normalize(String s) {
            return s == null ? "" : s.strip();
        }

        private int parseDigits(String s) {
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        }
    }

    @Test
    void defaultMethodUsesPrivateHelpers() {
        Parser parser = new Parser() {};
        assertEquals(42, parser.parse("  42 "));
    }

    @Test
    void defaultMethodCallsInterfaceStaticMethodByInterfaceName() {
        Parser parser = new Parser() {};

        assertEquals(10, parser.radix());
        assertEquals(10, Parser.defaultRadix());
    }
}
