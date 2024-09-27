package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.AppConfig;
import org.example.entities.FDProperties;
import org.example.entities.Message;
import org.example.service.*;
import org.example.service.Log.GrepExecutor;
import org.example.service.Ping.PingReceiver;
import org.example.service.Ping.PingSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class contains Server related logic
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public void startServer(){
        // Start the Log Server
//        LogServer logServer = new LogServer();
//        logServer.start();

        //Not using the below code as I have merged the introducer code with the PingReceiver.
        //if the server is a introducer you can use execute below statement
//        //TODO Pick value from properties to check if the server is introducer or not and below
//        if(true){
//            try {
//                Introducer introducer = new Introducer();
//                introducer.start();
//            }catch (SocketException e){
//                throw new RuntimeException(e);
//            }
//        }
        //TODO code to introduce itself
        if(!((Boolean) FDProperties.getFDProperties().get("isIntroducer"))) {
            PingSender pingSender = new PingSender();
            Map<String, Object> messageContent = new HashMap<>();
            messageContent.put("messageName", "alive");
            messageContent.put("senderName", (String) FDProperties.getFDProperties().get("machineName"));
            messageContent.put("senderIp", (String) FDProperties.getFDProperties().get("machineIp"));
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
        //Below code will start the Dissemination Service
        Dissemination dissemination = new Dissemination();
        dissemination.startDisseminatorService();


        /* WE can keep pinging the nodes in a loop so that we will get the responses from all healthy nodes quickly and then wait
        for faulty nodes to reply through swim mechanism. Doing this we can achieve time bounded completeness.
        We can also do a thing like in one loop ping all members within 5 seconds and after completion of 5 secs then randomize the
        list and start the pinging process again.
        */
        try {
            Thread.sleep(10000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //start the Failure detector scheduler
        if(((Boolean) FDProperties.getFDProperties().get("isIntroducer"))) {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            FDServer task = new FDServer(dissemination);
            executor.scheduleAtFixedRate(task.send(), 0, 10, TimeUnit.SECONDS);
//        executor.scheduleAtFixedRate(task.send(), 0, (int) FDProperties.getFDProperties().get("protocolPeriod"), TimeUnit.SECONDS);
        }
    }
}
