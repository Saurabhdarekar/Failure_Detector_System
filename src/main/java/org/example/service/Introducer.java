//package org.example.service;
//
//import org.example.Client;
//import org.example.entities.Member;
//import org.example.entities.MembershipList;
//import org.example.entities.Message;
//import org.example.service.Ping.PingSender;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//
//public class Introducer extends Thread {
//    private static final Logger logger = LoggerFactory.getLogger(Introducer.class);
//
//    private final DatagramSocket serverSocket;
//    private byte[] buf = new byte[256];
//
//    public Introducer() throws SocketException {
//        serverSocket = new DatagramSocket(4446);
//    }
//
//    public void run() {
//        boolean running = true;
//        System.out.println("Introducer server started");
//        while (running) {
//            DatagramPacket packet = new DatagramPacket(buf, buf.length);
//            try {
//                serverSocket.receive(packet);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            InetAddress address = packet.getAddress();
//            int port = packet.getPort();
//            packet = new DatagramPacket(buf, buf.length, address, port);
//            String received = new String(
//                    packet.getData(), 0, packet.getLength());
//
//            Message message = Message.process(address, port, received);
//            logger.info("A node wants to join a group with ip address " + packet.getAddress() + ":" + packet.getPort()
//                    + " with version " + message);
//            String result = "";
//            //if a node sends an alive message to join the group then multicast that message to everyone
//            if(message.getMessageName().equals("alive")) {
//                try {
//                    PingSender pingSender = new PingSender();
//                    result = pingSender.multicast(message);
//                    //add this member to its own list
//                    MembershipList.addMember(
//                            new Member(address.toString(), port, message.getMessageContent().get(0))
//                    );
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if(result.equals("Successful")) {
//                logger.info("Member Introduced in the Group");
//            }
//        }
//        serverSocket.close();
//    }
//
////    public static StringBuilder data(byte[] a)
////    {
////        if (a == null)
////            return null;
////        StringBuilder ret = new StringBuilder();
////        int i = 0;
////        while (a[i] != 0)
////        {
////            ret.append((char) a[i]);
////            i++;
////        }
////        return ret;
////    }
//}
