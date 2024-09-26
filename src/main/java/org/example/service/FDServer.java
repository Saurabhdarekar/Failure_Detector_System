package org.example.service;

import org.example.entities.FDProperties;
import org.example.entities.Member;
import org.example.entities.MembershipList;
import org.example.entities.Message;
import org.example.service.Ping.PingSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
                //TODO write a piece of code that will select a random number out of total Members present in the list
                Member member = MembershipList.getRandomMember();
                if(member == null) {
                    logger.error("MemberList is null!");
                    return;
                }
                //TODO send a ping message to this node
                //TODO edit the message
                Map<String,Object> messageContent = new HashMap<>();
                messageContent.put("messageName", "ping");
                messageContent.put("senderIp", (String) FDProperties.getFDProperties().get("machineIp"));
                messageContent.put("senderPort", (String) FDProperties.getFDProperties().get("machinePort"));
                messageContent.put("msgId", FDProperties.generateRandomMessageId());
                try {
                    Message message = new Message("ping", member.getIpAddress(), member.getPort(), messageContent);
                    //The timeout period for this connection will be (t)
                    PingSender pingSender = new PingSender(message);
                    pingSender.start();
                    pingSender.join();
                    String response = pingSender.getResult();
                    if(response.equals("Successful")) {
                        //TODO if the node has been marked as Suspected then ask the Disseminator to spread ALive message
                    } else if (response.equals("Unsuccessful")) {
                        //TODO code for probing k nodes, make the k global
                        int k = 3;
                        ArrayList<Member> randomMembers = new ArrayList<Member>();
                        ArrayList<PingSender> pingSenders = new ArrayList<PingSender>();
                        //The timeout period for these connections will be (T-t)
                        while (randomMembers.size() < k) {
                            //TODO now send ping to other k nodes
                            Member kmember = MembershipList.getRandomMember();
                            if (!randomMembers.contains(kmember)) { // ensure uniqueness
                                randomMembers.add(kmember);
//                                PingSender pingSender1 = new PingSender()
                            }
                            /////////////////
                        }
                        //TODO write a code which will generate
                        //TODO if you receive a successful ack from anyone of the nodes
                        Boolean responseFromKNodes = false;
                        if(responseFromKNodes){
                            ///TODO if the node has been marked as Suspected then ask the Disseminator to spread ALive message
                        }else{
                            Boolean ifSuspicionModeOn = false;
                            if(ifSuspicionModeOn){
                                //TODO put the node in the suspect mode
                                //TODO call the disseminator to spread the suspect message
                            }else{
                                //TODO remove the node from the list
                                //TODO call the disseminator to spread the fail message
                            }
                        }
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //TODO go through the list and is we don't receive an ack for any suspected node in our list then mark it as failed and send a multicast.

            }
        };
        return failureDetector;
    }
}
