package com.example.PasswordManagerWebApp.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Anurra
 */
public class GenericQueue<T> {
    private Queue<T> queue = new LinkedList<>();

    public void enqueue(T item) {
        queue.add(item);
    }

    public T dequeue() {
        return queue.isEmpty() ? null : queue.poll();
    }

    public Queue<T> getQueue() {
        return queue;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}