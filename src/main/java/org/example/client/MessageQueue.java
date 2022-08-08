package org.example.client;

import org.example.protocol.Message;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A message queue store sending request, will compare with response to identify the response
 * validation and check expired request.
 */
public class MessageQueue extends Thread {

    private final Queue<Map<Message, Long>> messageQueue;

    /**
     * Construct a queue with 50 capacity.
     */
    public MessageQueue() {
        messageQueue = new ArrayBlockingQueue<>(50);
    }

    @Override
    public void run() {
        // check the message that are not polled in 2 seconds, which will be considered as non responded
        // request then log it
        while (true) {
            // check if queue is empty
            if (!messageQueue.isEmpty()) {
                Map<Message, Long> head = messageQueue.peek();
                // check expired
                checkExpired(head);
            }
            // sleep
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ClientLogger.print("Message queue is sleeping");
            }
        }
    }

    /**
     * Check the target object is expired or not. Once a request is sent out, if it doesn't receive
     * the corresponding response will be expired and remove from the queue.
     *
     * @param target the target message with time stamp
     */
    public void checkExpired(Map<Message, Long> target) {
        long currentTimeMillis = System.currentTimeMillis();
        // if time is greater than 2s, it will be logged and remove from the queue
        if (Math.subtractExact(currentTimeMillis, (long) target.values().toArray()[0]) >= 5000) {
            ClientLogger.nonRespondedRequestLog((Message) target.keySet().toArray()[0]);
            // remove from the queue
            messageQueue.poll();
        }
    }

    /**
     * Add a message with time to the queue.
     *
     * @param object object
     */
    public void enqueue(Map<Message, Long> object) {
        messageQueue.add(object);
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */
    public Map<Message, Long> peek() {
        return messageQueue.peek();
    }

    /**
     * Remove the head from the queue.
     */
    public void poll() {
        messageQueue.poll();
    }
}
