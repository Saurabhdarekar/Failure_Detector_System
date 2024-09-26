package org.example.entities;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MembershipList {

    public static ConcurrentHashMap<String, Member> members = new ConcurrentHashMap<>();

    public static void addMember(Member member) {
        members.put(member.getName(), member);
    }

    public static void removeMember(String address) {
        members.remove(address);
    }

    public static void printMembers() {
        members.forEach((k, v) -> {System.out.println(k + ": " + v.port);});
    }

    public static Member getRandomMember() {
        List<String> keys = new ArrayList<>(members.keySet());
        // Check if map is not empty
        if (!keys.isEmpty()) {
            // Get a random key
            String randomKey = keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
            // Get the corresponding value
            Member member = members.get(randomKey);
            return new Member(member.getIpAddress(), member.getPort(), member.getVersionNo());
        } else {
            System.out.println("Map is empty.");
        }
        return null;
    }
}
