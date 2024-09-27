package org.example.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Member {
    String name;
    String ipAddress;
    String port;
    String versionNo;
    String status;


    String dateTime;

    public Member(String ipAddress, String port, String versionNo) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.versionNo = versionNo;
    }

    public Member(String name, String ipAddress, String port, String versionNo, String status, String dateTime) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.versionNo = versionNo;
        this.status = status;
        this.dateTime = dateTime;
    }

    public static String getLocalDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:nnnn");
        LocalDateTime dateTime = java.time.LocalDateTime.now();
        return dateTime.format(formatter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
