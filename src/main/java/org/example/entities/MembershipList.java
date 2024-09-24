package org.example.entities;

import java.net.InetAddress;
import java.util.ArrayList;

public class MembershipList {

    public static ArrayList<Member> members = new ArrayList<>();

    public static void addMember(Member member) { members.add(member); }

    public static void removeMember(InetAddress address) {
        members.removeIf(member -> member.ipAddress.equals(address.getHostAddress()));
    }

    public static ArrayList<Member> getMembers() { return new ArrayList<>(members); }
}
