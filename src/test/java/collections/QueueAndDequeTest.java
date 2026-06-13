package collections;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * put / take → 死等，不达目的不罢休
 * offer(time) / poll(time) → 限时等，超时认输
 * offer() / poll() → 不等，立即返回结果
 *
 */
class QueueAndDequeTest {

    @Test
    void arrayDequeAsStackAndQueue() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.offerLast("a");
        deque.offerLast("b");
        assertEquals("a", deque.pollFirst());
        deque.push("top");
        assertEquals("top", deque.pop());
    }

    @Test
    void priorityQueuePollsSmallestFirst() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(30);
        pq.offer(10);
        pq.offer(20);
        assertEquals(10, pq.poll());
        assertEquals(20, pq.poll());
        assertEquals(30, pq.poll());
    }

    @Test
    void priorityQueueWithComparator() {
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.reverseOrder());
        pq.offer("a");
        pq.offer("c");
        pq.offer("b");
        assertEquals("c", pq.poll());
        assertEquals("b", pq.poll());
    }

    @Test
    void queueFifoOrder() {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(1);
        q.offer(2);
        assertEquals(1, q.poll());
        assertEquals(2, q.peek());
        assertEquals(2, q.poll());
        assertNull(q.poll());
    }
}
