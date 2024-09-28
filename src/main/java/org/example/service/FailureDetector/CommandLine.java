package org.example.service.FailureDetector;

import java.util.concurrent.ConcurrentHashMap;

// Comma class that will be executed by multiple threads
public class CommandLine implements Runnable {
    private ConcurrentHashMap<String, Integer> map;
    private String threadName;

    // Constructor to initialize the task with a map and a thread name
    public CommandLine(ConcurrentHashMap<String, Integer> map, String threadName) {
        this.map = map;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        // Each thread updates the map with its own name as the key

    }
}
