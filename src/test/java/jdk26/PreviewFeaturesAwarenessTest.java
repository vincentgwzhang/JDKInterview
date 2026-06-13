package jdk26;

import org.junit.jupiter.api.Test;

import java.lang.Runtime.Version;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JDK 26 特性仍在演进；本测试用于确认运行环境并演示「LTS vs 非 LTS」面试题答案。
 */
class PreviewFeaturesAwarenessTest {

    @Test
    void runtimeVersionAndLtsStrategy() {
        Version version = Runtime.version();
        int feature = version.feature();
        assertTrue(feature >= 21, "建议 JDK 21+ 运行本仓库");

        boolean isLts = feature == 8 || feature == 11 || feature == 17
                || feature == 21 || feature == 25;
        // 26 非 LTS
        if (feature == 26) {
            assertFalse(isLts);
        }
    }

    @Test
    void virtualThreadsAvailableFrom21() {
        Thread.Builder builder = Thread.ofVirtual().name("vt-", 0);
        Thread t = builder.unstarted(() -> {});
        assertTrue(t.isVirtual());
    }
}
