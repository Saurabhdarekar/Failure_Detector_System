package org.example;

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
                        Integer.parseInt((String) FDProperties.getFDProperties().get("introducerPort")),
                        messageContent);
                pingSender.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Below code will start the Dissemination Service
        Dissemination dissemination = new Dissemination();
        dissemination.startDisseminatorService();

        //start the Failure detector scheduler
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        FDServer task = new FDServer(dissemination);
//        executor.scheduleAtFixedRate(task.send(), 0, (int) FDProperties.getFDProperties().get("protocolPeriod"), TimeUnit.SECONDS);
    }
}
