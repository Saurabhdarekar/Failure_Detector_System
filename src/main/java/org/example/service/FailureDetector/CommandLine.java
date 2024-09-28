package org.example.service.FailureDetector;

import org.example.Client;
import org.example.Server;
import org.example.entities.FDProperties;
import org.example.entities.MembershipList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

// Comma class that will be executed by multiple threads
public class CommandLine implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CommandLine.class);
    private ConcurrentHashMap<String, Integer> map;
    private String threadName;


    @Override
    public void run() {
        // Each thread updates the map with its own name as the key
        while(true) {
            System.out.println("Enter The command");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            Dissemination d = new Dissemination();
            try {
                if (command.startsWith("grep_log")) {
                    int index = command.indexOf("grep_log");
                    if (index != -1) {
                        String grepCommand = command.substring(index + "grep_log".length()).trim();
                        Client c = new Client();
                        c.runClient(grepCommand);
                    }
                } else {
                    switch (command) {
                        case "list_mem":
                            System.out.println("Membership List");
                            MembershipList.printMembersId();
                            break;

                        case "list_self":
                            System.out.println("Node self id");
                            String id = FDProperties.getFDProperties().get("machineIp") + String.valueOf(FDProperties.getFDProperties().get("machinePort"))
                                    + FDProperties.getFDProperties().get("versionNo");
                            System.out.println(id);
                            break;
                        case "join":
                            System.out.println("Joining Node");
                            Server s = new Server();
                            s.startServer();
                            break;
                        case "leave":
                            d.sendLeaveMessage();
                            break;

                        case "enable_sus":

                        case "disable_sus":
                            d.sendSwitch();
                            break;

                        case "status_sus":
                            System.out.println(FDProperties.getFDProperties().get("isSuspicionModeOn"));
                            break;

                        default:
                            System.out.println("Invalid command");
                            logger.error("Invalid command");


                    }
                }


            } catch (Exception e) {
                logger.error("Error in Commandline while exectuing  command {}  Error  : {}", command, e);
            }
        }
    }
}
