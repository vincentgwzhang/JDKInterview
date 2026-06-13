package threading;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class WaitNotifyTest {

    static class BoundedBuffer {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity = 5;

        synchronized void put(int value) throws InterruptedException {
            while (queue.size() >= capacity) {
                wait();
            }
            queue.offer(value);
            notifyAll();
        }

        synchronized int take() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            int v = queue.poll();
            notifyAll();
            return v;
        }
    }

    @Test
    void producerConsumerWithWaitNotify() throws Exception {
        BoundedBuffer buffer = new BoundedBuffer();
        CountDownLatch consumed = new CountDownLatch(10);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    buffer.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    assertEquals(i, buffer.take());
                    consumed.countDown();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        producer.start();
        producer.join(3000);
        consumer.join(3000);
        assertTrue(consumed.await(1, TimeUnit.SECONDS));
    }
}
