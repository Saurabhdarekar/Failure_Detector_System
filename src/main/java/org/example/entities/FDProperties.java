package org.example.entities;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.config.AppConfig.readPropertiesFile;

public class FDProperties {
    public static ConcurrentHashMap<String, Object> fDProperties = new ConcurrentHashMap<>();

    public static void initialize(){
        Properties prop = null;
        try {
            prop = readPropertiesFile("application.properties");
        }catch (IOException e){
            e.printStackTrace();
        }
        if(prop == null){
            throw new RuntimeException("Could not load application properties file");
        }
        fDProperties.put("isIntroducer",Boolean.parseBoolean(prop.getProperty("isIntroducer")));
        fDProperties.put("protocolPeriod", Integer.parseInt(prop.getProperty("protocolPeriod")));
        fDProperties.put("ackWaitPeriod", Integer.parseInt(prop.getProperty("ackWaitPeriod")));
        fDProperties.put("suspicionProtocolPeriod", Integer.parseInt(prop.getProperty("suspicionProtocolPeriod")));
        fDProperties.put("noOfProbingNodes", Integer.parseInt(prop.getProperty("noOfProbingNodes")));
        fDProperties.put("isSuspicionModeOn", Boolean.parseBoolean(prop.getProperty("suspicionModeOn")));
        fDProperties.put("machineIp", prop.getProperty("machineIp"));
        fDProperties.put("machinePort", Integer.parseInt(prop.getProperty("machinePort")));
        fDProperties.put("machineReceivingPort", Integer.parseInt(prop.getProperty("machineReceivingPort")));
        fDProperties.put("introducerAddress", prop.getProperty("introducerAddress"));
        fDProperties.put("introducerPort", prop.getProperty("introducerPort"));
        fDProperties.put("machineName", prop.getProperty("machineName"));
    }

    public static ConcurrentHashMap<String, Object> getFDProperties(){
        return fDProperties;
    }

    public static void printFDProperties(){
        System.out.println("FDProperties:");
        fDProperties.forEach((key, value) -> System.out.println(key + "=" + value));
    }

    public static String generateRandomMessageId(){
        Random random = new Random();
        return String.valueOf(1000000000L + (long)(random.nextDouble() * 9000000000L));
    }
}
