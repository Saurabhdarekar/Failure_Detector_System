package org.example.service.Ping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.FDProperties;
import org.example.entities.Member;
import org.example.entities.MembershipList;
import org.example.entities.Message;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class PingSender extends Thread {

    private volatile String result;
    private Message message;

    public PingSender(Message message) {
        this.message = message;
    }

    public PingSender() {}

    public String getResult() {return result; }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            //This node will wait for below timeout for the target node to send the ack
            socket.setSoTimeout((int)FDProperties.getFDProperties().get("ackWaitPeriod"));
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(message.getMessageContent());
            byte[] buf = s.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, message.getIpAddress(), message.getPort());
            socket.send(packet);

            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            Message replyMessage = Message.process(address, port, received);
            if (replyMessage.getMessageContent().get("messageName").equals("pingAck")) {
                result = "Successful";
            }
        }
        catch (SocketTimeoutException e) {
            // as the message time has timeout we will send an unsuccessful response
            result = "Unsuccessful";
        } catch (IOException e) {
            throw new RuntimeException("IOException");
        }
    }

    public String sendPing(Message  message) {
        try {
            DatagramSocket socket = new DatagramSocket();
            //This node will wait for below timeout for the target node to send the ack
            socket.setSoTimeout((int)FDProperties.getFDProperties().get("ackWaitPeriod"));
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(message.getMessageContent());
            byte[] buf = s.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, message.getIpAddress(), message.getPort());
            socket.send(packet);

            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            Message replyMessage = Message.process(address, port, received);
            if (replyMessage.getMessageContent().get("messageName").equals("pingAck")) {
                return "Successful";
            }
        }
        catch (SocketTimeoutException e) {
            // as the message time has timeout we will send an unsuccessful response
            return "Unsuccessful";
        } catch (IOException e) {
            return "IOException";
        }
        return "Unsuccessful";
    }

    public String sendMessage(Message message) {
        try {
            DatagramSocket socket = new DatagramSocket();
            //This node will wait for below timeout for the target node to send the ack
//            socket.setSoTimeout((int)FDProperties.getFDProperties().get("ackWaitPeriod"));
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(message.getMessageContent());
            byte[] buf = s.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, message.getIpAddress(), message.getPort());
            socket.send(packet);
        }
        catch (SocketTimeoutException e) {
            // as the message time has timeout we will send an unsuccessful response
            return "Unsuccessful";
        } catch (IOException e) {
            return "IOException";
        }
        return "Unsuccessful";
    }

    public String multicast(Message multicastMessage) throws IOException {
        ConcurrentHashMap<String, Member> copiedMap = new ConcurrentHashMap<>(MembershipList.members);
        copiedMap.forEach((key, member) -> {
            Message message = null;
            try {
                message = new Message(multicastMessage.getMessageName(), member.getIpAddress(),member.getPort(),
                        multicastMessage.getMessageContent());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            sendPing(message);
        });
        return "Successful";
    }
//    public String sendEcho(String msg) throws IOException {
//        buf = msg.getBytes();
//        DatagramPacket packet
//                = new DatagramPacket(buf, buf.length, address, 4445);
//        socket.send(packet);
//        packet = new DatagramPacket(buf, buf.length);
//        socket.receive(packet);
//        String received = new String(
//                packet.getData(), 0, packet.getLength());
//        System.out.println(packet.getLength() );
//        return received;
//    }

    public void close() {
//        socket.close();
    }
}
