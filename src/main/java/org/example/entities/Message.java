package org.example.entities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Message {
    String messageName;
    InetAddress ipAddress;
    int port;
    ArrayList<String> messageContent;

    public Message(String messageName, String ipAddress, int port, ArrayList<String> messageContent) throws UnknownHostException {
        this.messageName = messageName;
        this.ipAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        this.messageContent = messageContent;
    }

    public Message(String messageName, InetAddress ipAddress, int port, ArrayList<String> messageContent) {
        this.messageName = messageName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.messageContent = messageContent;
    }

    public static Message process(InetAddress address, int port, String input) {
        String[] words = input.split("|");
        ArrayList<String> messageContent = new ArrayList<>(Arrays.asList(words).subList(1, words.length));
        return new Message(words[0], address, port, messageContent);
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) throws UnknownHostException {
        this.ipAddress = InetAddress.getByName(ipAddress);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ArrayList<String> getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(ArrayList<String> messageContent) {
        this.messageContent = messageContent;
    }
}
