package org.example.service.Ping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.FDProperties;
import org.example.entities.Member;
import org.example.entities.MembershipList;
import org.example.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class PingReceiver extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(PingReceiver.class);

    private DatagramSocket serverSocket;
    private boolean running;
    private byte[] buf = new byte[256];

    public PingReceiver() throws SocketException {
        serverSocket = new DatagramSocket((int)FDProperties.getFDProperties().get("machinePort"));
    }

    public void run() {
        running = true;
        System.out.println("Listener service for Dissemination Component started");
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
            //TODO add the introducer under this class and set a flag of if introducer then only execute the below code
            //TODO same will be for suspicion mode.
            //Add a switch case
            switch (message.getMessageName()){
                case "introduction":
                    logger.info("Introduction successful message received");
                    MembershipList.addMember(
                            new Member((String) message.getMessageContent().get("senderName"),
                                    (String) message.getMessageContent().get("senderIp"),
                                    Integer.parseInt((String) message.getMessageContent().get("senderPort")),
                                    (String) message.getMessageContent().get("version"))
                    );
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String,Member> map = mapper.convertValue(message.getMessageContent(), Map.class);
                    map.forEach((k,v) -> {System.out.println(k+":"+v);});
                    MembershipList.printMembers();
                case "alive":
                    if(message.getMessageContent().get("isIntroducing").equals("true") && (Boolean) FDProperties.getFDProperties().get("isIntroducer")){
                        logger.info("A node wants to join a group with ip address " + packet.getAddress() + ":" + packet.getPort()
                                + " with version " + message);
                        String result = "";
                        //if a node sends an alive message to join the group then multicast that message to everyone
                        try {
                            PingSender pingSender = new PingSender();
                            result = pingSender.multicast(message);
                            //add this member to its own list
                            MembershipList.addMember(
                                    new Member((String) message.getMessageContent().get("senderName"),
                                            (String) message.getMessageContent().get("senderIp"),
                                            Integer.parseInt((String) message.getMessageContent().get("senderPort")),
                                            (String) message.getMessageContent().get("version"))
                            );
                            MembershipList.printMembers();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if(result.equals("Successful")) {
                            logger.info("Member Introduced in the Group");
                            PingSender pingSender = new PingSender();
                            Map<String, Object> messageContent = new HashMap<>();
                            messageContent.put("messageName", "introduction");
                            messageContent.put("senderName", (String) FDProperties.getFDProperties().get("machineName"));
                            messageContent.put("senderIp", (String) FDProperties.getFDProperties().get("machineIp"));
                            messageContent.put("senderPort", String.valueOf(FDProperties.getFDProperties().get("machinePort")));
                            messageContent.put("msgId", FDProperties.generateRandomMessageId());
                            messageContent.put("isIntroducing", "false");
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                String json = objectMapper.writeValueAsString(messageContent);
                                messageContent.put("members", json);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                Message introduceBackMessage = new Message("alive",
                                        (String) message.getMessageContent().get("senderIp"),
                                        Integer.parseInt((String) message.getMessageContent().get("senderPort")),
                                        messageContent);
                                pingSender.sendMessage(introduceBackMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        logger.info("Alive message received");
                        MembershipList.addMember(
                                new Member((String) message.getMessageContent().get("senderName"),
                                        (String) message.getMessageContent().get("senderIp"),
                                        Integer.parseInt((String) message.getMessageContent().get("senderPort")),
                                        (String) message.getMessageContent().get("version"))
                        );
                        MembershipList.printMembers();
                    }
                    break;
                case "failed" :
                    logger.info("Failed message received");
                    MembershipList.removeMember((String) message.getMessageContent().get("senderIp"));
                    break;
                case "suspect" :
                    logger.info("Suspect message received");
                    //TODO add a piece of code to set the status of a member to suspect
                    //TODO add a piece of code which will send alive multicast if the suspect node is itself
                    break;
                case "confirm" :
                    logger.info("Confirm message received");
                    //TODO add a piece of code that will remove the member from the list
//                    MembershipList.removeMember(address);
                    break;
                default:
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
