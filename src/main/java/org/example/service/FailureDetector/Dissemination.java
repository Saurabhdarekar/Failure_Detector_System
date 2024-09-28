package org.example.service.FailureDetector;

import org.example.entities.FDProperties;
import org.example.entities.Member;
import org.example.entities.MembershipList;
import org.example.entities.Message;
import org.example.service.Ping.PingReceiver;
import org.example.service.Ping.PingSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Dissemination {

    private static final Logger logger = LoggerFactory.getLogger(Dissemination.class);

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

    public void sendSuspectMessage(Member member) {
        try {
            member.setStatus("Suspected");
            member.setDateTime(Member.getLocalDateTime());
            //TODO add the incarnation number
            MembershipList.members.put(member.getName(), member);
            logger.info("Node is Suspected : {}", member.getName());
            Map<String, Object> messageContent = new HashMap<>();
            messageContent.put("messageName", "suspect");
            messageContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
            messageContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
            messageContent.put("msgId", FDProperties.generateRandomMessageId());
            messageContent.put("memberName", member.getName());
            messageContent.put("memberIp", member.getIpAddress());
            messageContent.put("memberPort", member.getPort());
            PingSender removeNode = new PingSender();
            removeNode.multicast("suspect", messageContent);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void sendSelfAliveMessage() {
        try {
            logger.info("Setting self alive");
            Map<String, Object> messageContent = new HashMap<>();
            messageContent.put("messageName", "alive");
            messageContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
            messageContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
            messageContent.put("msgId", FDProperties.generateRandomMessageId());
            messageContent.put("memberName", FDProperties.getFDProperties().get("machineName"));
            messageContent.put("memberIp", FDProperties.getFDProperties().get("machineIp"));
            messageContent.put("memberPort", FDProperties.getFDProperties().get("machinePort"));
            PingSender pingSender = new PingSender();
            pingSender.multicast("alive", messageContent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAliveMessageToIntroducer(){
        PingSender pingSender = new PingSender();
        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("messageName", "alive");
        messageContent.put("senderName", FDProperties.getFDProperties().get("machineName"));
        messageContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
        messageContent.put("senderPort", String.valueOf(FDProperties.getFDProperties().get("machinePort")));
        messageContent.put("msgId", FDProperties.generateRandomMessageId());
        messageContent.put("isIntroducing", "true");
        try {
            logger.info("Sending alive message to introducer");
            Message message = new Message("alive",
                    (String) FDProperties.getFDProperties().get("introducerAddress"),
                    ((String) FDProperties.getFDProperties().get("introducerPort")),
                    messageContent);
            pingSender.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAliveMessage(Member member) {
        try {
            member.setStatus("Alive");
            member.setDateTime(Member.getLocalDateTime());
            MembershipList.members.put(member.getName(), member);
            logger.info("Node is Alive : {}", member.getName());
            Map<String, Object> messageContent = new HashMap<>();
            messageContent.put("messageName", "alive");
            messageContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
            messageContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
            messageContent.put("msgId", FDProperties.generateRandomMessageId());
            messageContent.put("memberName", member.getName());
            messageContent.put("memberIp", member.getIpAddress());
            messageContent.put("memberPort", member.getPort());
            PingSender pingSender = new PingSender();
            pingSender.multicast("alive", messageContent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFailedMessage(Member removeMember) {
        try {
            MembershipList.removeMember(removeMember.getName());
            logger.info("Node has Failed : " + removeMember.getName());
            Map<String, Object> removeContent = new HashMap<>();
            removeContent.put("messageName", "failed");
            removeContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
            removeContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
            removeContent.put("msgId", FDProperties.generateRandomMessageId());
            removeContent.put("memberName", removeMember.getName());
            removeContent.put("memberIp", removeMember.getIpAddress());
            removeContent.put("memberPort", removeMember.getPort());
            PingSender removeNode = new PingSender();
            removeNode.multicast("failed", removeContent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendConfirmMessage(Member removeMember) {
        try {
            MembershipList.removeMember(removeMember.getName());
            logger.info("Node was in Suspect state and since there is no Alive message, Node Failed : " + removeMember.getName());
            Map<String, Object> removeContent = new HashMap<>();
            removeContent.put("messageName", "confirm");
            removeContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
            removeContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
            removeContent.put("msgId", FDProperties.generateRandomMessageId());
            removeContent.put("memberName", removeMember.getName());
            removeContent.put("memberIp", removeMember.getIpAddress());
            removeContent.put("memberPort", removeMember.getPort());
            PingSender removeNode = new PingSender();
            removeNode.multicast("confirm", removeContent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
