package threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ThreadLocalTest {

    private static final ThreadLocal<String> CTX = new ThreadLocal<>();

    @Test
    void eachThreadHasOwnCopy() throws Exception {
        CountDownLatch done = new CountDownLatch(2);
        String[] t1Val = new String[1];
        String[] t2Val = new String[1];

        Thread a = new Thread(() -> {
            CTX.set("A");
            t1Val[0] = CTX.get();
            CTX.remove();
            done.countDown();
        });
        Thread b = new Thread(() -> {
            CTX.set("B");
            t2Val[0] = CTX.get();
            CTX.remove();
            done.countDown();
        });
        a.start();
        b.start();
        assertTrue(done.await(2, TimeUnit.SECONDS));
        assertEquals("A", t1Val[0]);
        assertEquals("B", t2Val[0]);
        assertNull(CTX.get());
    }
}
