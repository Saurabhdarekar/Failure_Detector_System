package org.example;

import org.example.entities.FDProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is main entry point
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        FDProperties.initialize();
        FDProperties.printFDProperties();
        Server s = new Server();
        s.startServer();
        System.out.println("Server started");
    }
}