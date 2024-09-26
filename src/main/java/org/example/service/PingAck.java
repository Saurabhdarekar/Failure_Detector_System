package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class PingAck {
    public static void main(String[] args) {
        // Create a ConcurrentHashMap
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // Create threads that will access and update the map
        Thread t1 = new Thread(new Task(map, "Thread-1"));
        Thread t2 = new Thread(new Task(map, "Thread-2"));
        Thread t3 = new Thread(new Task(map, "Thread-3"));

        // Start the threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to complete
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Print the final state of the map
        System.out.println("Final Map State:");
        map.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}