package org.example.service;

public class FDServer {
    public Runnable send() {
        Runnable failureDetector = new Runnable() {
            @Override
            public void run() {
                logger.info(" failed");
            }
        };
        return failureDetector;
    }
}
