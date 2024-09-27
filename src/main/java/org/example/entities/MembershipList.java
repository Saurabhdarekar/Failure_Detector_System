package org.example.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class MembershipList {

    public static ConcurrentHashMap<String, Member> members = new ConcurrentHashMap<>();

    public static void addMember(Member member) {
        members.put(member.getName(), member);
    }

    public static void removeMember(String name) {
        members.remove(name);
    }

    public static void printMembers() {
        members.forEach((k, v) -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(v);
                System.out.println(k + ": " + json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Member getRandomMember() {
        List<String> keys = new ArrayList<>(members.keySet());
        // Check if map is not empty
        if (!keys.isEmpty()) {
            // Get a random key and the corresponding value
            Member member = members.get(keys.get(ThreadLocalRandom.current().nextInt(keys.size())));
            return new Member(member.getName(),
                    member.getIpAddress(),
                    member.getPort(),
                    member.getVersionNo(),
                    member.getStatus(),
                    member.getDateTime());
        } else {
            System.out.println("Map is empty.");
        }
        return null;
    }
}
