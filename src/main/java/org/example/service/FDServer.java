package org.example.service;

import org.example.entities.FDProperties;
import org.example.entities.Member;
import org.example.entities.MembershipList;
import org.example.entities.Message;
import org.example.service.Ping.PingSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FDServer {
    private static final Logger logger = LoggerFactory.getLogger(LogServer.class);
    private Dissemination dissemination;

    public FDServer(Dissemination dissemination) {
        this.dissemination = dissemination;
    }

    public Runnable send() {
        Runnable failureDetector = new Runnable() {
            @Override
            public void run() {
                System.out.println("Running loop");
                MembershipList.generateRandomList();
                while (MembershipList.isLast()) {
                    System.out.println(MembershipList.isLast());
                    //TODO write a piece of code that will select a random number out of total Members present in the list
                    Member member = MembershipList.getRandomMember();
                    System.out.println("Member selected: " + member.getName());
                    if(member == null) {
                        logger.error("MemberList is null!");
                        return;
                    }
                    System.out.println("Sending ping");
                    //TODO send a ping message to this node
                    //TODO edit the message
                    Map<String,Object> messageContent = new HashMap<>();
                    messageContent.put("messageName", "ping");
                    messageContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
                    messageContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
                    messageContent.put("msgId", FDProperties.generateRandomMessageId());
                    try {
                        Message message = new Message("ping", member.getIpAddress(), member.getPort(), messageContent);
                        //The timeout period for this connection will be (t)
                        PingSender pingSender = new PingSender(message, (int) FDProperties.getFDProperties().get("ackWaitPeriod"));
                        pingSender.start();
                        pingSender.join();
                        String response = pingSender.getResult();
                        System.out.println("response is" + response);
                        if(response.equals("Successful")) {
                            if((Boolean) FDProperties.getFDProperties().get("isSuspicionModeOn")){
                                //TODO if the node has been marked as Suspected then ask the Disseminator to spread ALive message
                            }
                        } else if (response.equals("Unsuccessful")) {
                            //TODO code for probing k nodes, make the k global
                            int k = 2;
                            //The timeout period for these connections will be (T-t)
                            int wait = (int) FDProperties.getFDProperties().get("suspicionProtocolPeriod") - (int) FDProperties.getFDProperties().get("ackWaitPeriod");
                            //Message to be sent
                            Map<String,Object> pingReqContent = new HashMap<>();
                            pingReqContent.put("messageName", "pingReq");
                            pingReqContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
                            pingReqContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
                            pingReqContent.put("msgId", FDProperties.generateRandomMessageId());
                            pingReqContent.put("targetSenderIp", member.getIpAddress());
                            pingReqContent.put("targetSenderPort", member.getPort());
                            List<Member> membersList = MembershipList.getKRandomMember(k, member.getName());
                            ArrayList<PingSender> pingSenders = new ArrayList<PingSender>();
                            for (int i = 0; i < membersList.size(); i++) {
                                Message pingReqMessage = new Message("pingReq", membersList.get(i).getIpAddress(), membersList.get(i).getPort(), pingReqContent);
                                PingSender pingSender1 = new PingSender(pingReqMessage, wait);
                                pingSender1.start();
                                pingSenders.add(pingSender1);
                            }
//                            while (membersList.size() < k) {
//                                //TODO now send ping to other k nodes
//                                Member kmember = MembershipList.getRandomMember();
//                                if (!randomMembers.contains(kmember)) { // ensure uniqueness
//                                    randomMembers.add(kmember);
//                                    Message pingReqMessage = new Message("pingReq", kmember.getIpAddress(), kmember.getPort(), pingReqContent);
//                                    PingSender pingSender1 = new PingSender(pingReqMessage, wait);
//                                    pingSender1.start();
//                                    pingSenders.add(pingSender1);
//                                }
//                            }
                            Boolean responseFromKNodes = false;
                            for(PingSender ps : pingSenders) {
                                try {
                                    ps.join();
                                    System.out.println(ps.getResult());
                                    responseFromKNodes = (ps.getResult().equals("Successful"));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            //TODO write a code which will generate
                            //TODO if you receive a successful ack from anyone of the nodes
                            if(responseFromKNodes){
                                ///TODO if the node has been marked as Suspected then ask the Disseminator to spread ALive message
                            }else{
                                if((Boolean) FDProperties.getFDProperties().get("isSuspicionModeOn")){
                                    //TODO put the node in the suspect mode
                                    //TODO call the disseminator to spread the suspect message
                                }else{
                                    //TODO remove the node from the list
                                    MembershipList.removeMember(member.getName());
                                    //TODO call the disseminator to spread the fail message
                                    System.out.println("Node has Failed");
                                    Map<String,Object> removeContent = new HashMap<>();
                                    removeContent.put("messageName", "failed");
                                    removeContent.put("senderIp", FDProperties.getFDProperties().get("machineIp"));
                                    removeContent.put("senderPort", FDProperties.getFDProperties().get("machinePort"));
                                    removeContent.put("msgId", FDProperties.generateRandomMessageId());
                                    removeContent.put("memberName", member.getName());
                                    removeContent.put("memberIp", member.getIpAddress());
                                    Message removeMessage = new Message("failed", member.getIpAddress(), member.getPort(), removeContent);
                                    PingSender removeNode = new PingSender();
                                    removeNode.multicast(removeMessage);
                                }
                            }
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //TODO go through the list and is we don't receive an ack for any suspected node in our list then mark it as failed and send a multicast.
                }
            }
        };
        return failureDetector;
    }
}
