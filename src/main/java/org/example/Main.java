package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is main entry point
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Server s = new Server();
        s.startServer();
        System.out.println("Server started");
    }
}