package org.example.service.Ping;

import org.example.entities.Member;
import org.example.entities.MembershipList;
import org.example.entities.Message;
import org.example.service.Introducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PingReceiver extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(PingReceiver.class);

    private DatagramSocket serverSocket;
    private boolean running;
    private byte[] buf = new byte[256];

    public PingReceiver() throws SocketException {
        serverSocket = new DatagramSocket(4445);
    }

    public void run() {
        running = true;
        System.out.println("Ping server started");
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                serverSocket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Ping received");
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(
                    packet.getData(), 0, packet.getLength());

            Message message = Message.process(address, port, received);

            //TODO based on the message received take the action
            //Add a switch case
            switch (message.getMessageName()){
                case "ping":
                    logger.info("Ping received");
                    try {
                        PingSender pingSender = new PingSender();
                        Message sendMessage = new Message("ping-ack", message.getIpAddress(), message.getPort(),
                                new ArrayList<>(Arrays.asList("alive", message.getMessageContent().get(1))));
                        String result = pingSender.sendPing(sendMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "alive":
                    logger.info("Alive message received");
                    MembershipList.addMember(
                            new Member(address.toString(), port, message.getMessageContent().get(0))
                    );
                    break;
                case "failed" :
                    logger.info("Failed message received");
                    MembershipList.removeMember(address);
                    break;
            }
        }
        serverSocket.close();
    }

    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
