package org.example;

import org.example.config.AppConfig;
import org.example.service.FDServer;
import org.example.service.Introducer;
import org.example.service.Log.GrepExecutor;
import org.example.service.LogServer;
import org.example.service.PingAck;
import org.example.service.Ping.PingReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class contains Server related logic
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    /**
     * This functions connects to Server and passes the command and then prints and save the result.
     */
    public void runClient(){
        Socket socket = null;
        ServerSocket server = null;

        AppConfig appConfig = new AppConfig();
        Properties properties = appConfig.readConfig();
        try
        {
            server = new ServerSocket(Integer.parseInt(properties.getProperty("port.number")));
            while(true) {
                ObjectOutputStream oos = null;
                String request = null;
                String response = "";
                logger.info("Server started");
                logger.info("Waiting for a Client to connect...");
                socket = server.accept();
                logger.info("Client __ is connected to Server");

                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                request = dataInputStream.readUTF();

                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("command received");

                // Code for executing the Grep Command
                GrepExecutor grepExecutor = new GrepExecutor(properties.getProperty("file.path"));
                List<String> responseList = grepExecutor.executeGrep(request);
                //TODO for now printing it later send it back to Client
                logger.info("No of Lines returned : " + responseList.size());
                try {
//                    while (!response.equals("Query Completed")) {
//
//                        if (responseList != null && !responseList.isEmpty()) {
//                            response = "Query Completed";
//                            logger.info(response);
//                            dataOutputStream.writeUTF(response);
//                            dataOutputStream.flush();
//                        }
//                        else {
//                            response = "Query Failed";
//                            dataOutputStream.writeUTF(response);
//                            dataOutputStream.flush();
//                        }
//
//                        oos = new ObjectOutputStream(socket.getOutputStream());
//                        oos.writeObject(responseList);
//                    }
                    response = "Query Completed";
                    logger.info(response);
                    dataOutputStream.writeUTF(response);
                    dataOutputStream.flush();
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(responseList);
                }
                catch (Exception e) {
                    dataOutputStream.writeUTF("Query Failed" + e.getMessage());
                    dataOutputStream.flush();
                    throw new RuntimeException("Query Failed" + e.getMessage());
                }

                dataOutputStream.close();
                dataInputStream.close();

                socket.close();
            }
//            Server.close();
        }
        catch(IOException i)
        {
            System.out.println(i.getMessage());
        }
    }

    public void startServer() {
        // Start the Log Server
        LogServer logServer = new LogServer();
        logServer.start();

        //if the server is a introducer you can use execute below statement
        //TODO Pick value from properties to check if the server is introducer or not and below
        if(true){
            try {
                Introducer introducer = new Introducer();
                introducer.start();
            }catch (SocketException e){
                throw new RuntimeException(e);
            }
        }

        //Start the Ping receiving service
        PingReceiver pingReceiver = null;
        try {
            pingReceiver = new PingReceiver();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        pingReceiver.start();

        //start the Failure detector scheduler
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        FDServer task = new FDServer();
//        executor.scheduleAtFixedRate(task.send(), 0, 1, TimeUnit.SECONDS);
    }
}
