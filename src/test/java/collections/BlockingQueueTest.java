package collections;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {

    @Test
    void arrayBlockingQueuePutTake() throws Exception {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        queue.put("job-1");
        queue.put("job-2");
        assertEquals("job-1", queue.take());
        assertEquals(1, queue.remainingCapacity());
        queue.offer("job-3", 100, TimeUnit.MILLISECONDS);
        assertEquals("job-2", queue.poll(1, TimeUnit.SECONDS));
    }

    @Test
    void producerConsumerWithBlockingQueue() throws Exception {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(3);
        CountDownLatch consumed = new CountDownLatch(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    queue.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    assertEquals(i, queue.take());
                    consumed.countDown();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        producer.start();
        producer.join(2000);
        consumer.join(2000);
        assertTrue(consumed.await(1, TimeUnit.SECONDS));
    }
}
