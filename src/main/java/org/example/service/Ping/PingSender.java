package org.example.service.Ping;

import org.example.entities.Member;
import org.example.entities.MembershipList;
import org.example.entities.Message;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PingSender {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public PingSender() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendPing(Message  message) throws IOException {
        String s = message.getMessageName();
        for(String mc : message.getMessageContent()){s = "|" + mc;}
        buf = s.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, message.getIpAddress(), message.getPort());
        socket.send(packet);
        //TODO add to the sending list which keep track of pings send, for which we are expecting ack
        // Add a switch case
        return "Successful";
    }

    public String multicast(ArrayList<Member> membershipList, Message multicastMessage) throws IOException {
        for(Member member : membershipList) {
            Message message = new Message(multicastMessage.getMessageName(), member.getIpAddress(),member.getPort(),
                    multicastMessage.getMessageContent());
            sendPing(message);
        }
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
        socket.close();
    }
}
