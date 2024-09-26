package org.example.service;

import org.example.service.Ping.PingReceiver;

import java.net.SocketException;

public class Dissemination {

    public void startDisseminatorService() {
        //Start the Ping receiving service
        PingReceiver pingReceiver = null;
        try {
            pingReceiver = new PingReceiver();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        pingReceiver.start();
    }

    public void sendMessage(String message) {
        //TODO write the code to multicast the message received from FD

    }
}
