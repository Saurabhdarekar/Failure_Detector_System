package org.example.entities;

public class Member {
    String name;
    String ipAddress;
    int port;
    String versionNo;
    String status;

    public Member(String ipAddress, int port, String versionNo) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.versionNo = versionNo;
    }

    public Member(String name, String ipAddress, int port, String versionNo) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.versionNo = versionNo;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
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
}
