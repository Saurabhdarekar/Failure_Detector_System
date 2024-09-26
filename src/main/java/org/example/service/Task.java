package org.example.service;

import java.util.concurrent.ConcurrentHashMap;

// Task class that will be executed by multiple threads
public class Task implements Runnable {
    private ConcurrentHashMap<String, Integer> map;
    private String threadName;

    // Constructor to initialize the task with a map and a thread name
    public Task(ConcurrentHashMap<String, Integer> map, String threadName) {
        this.map = map;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        // Each thread updates the map with its own name as the key
        for (int i = 0; i < 5; i++) {
            // Increment the value associated with the thread name
            map.merge(threadName, 1, Integer::sum); // safely increments the count for the thread name
            System.out.println(threadName + " updated the count: " + map.get(threadName));
            try {
                // Simulate some processing time
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
