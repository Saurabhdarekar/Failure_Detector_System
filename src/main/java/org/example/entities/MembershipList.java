package org.example.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class MembershipList {

    public static ConcurrentHashMap<String, Member> members = new ConcurrentHashMap<>();
    public static ArrayList<String> memberNames = new ArrayList<>();
    public static int pointer;

    public static void addMember(Member member) {
        members.put(member.getName(), member);
        memberNames.add(member.getName());
    }

    public static void removeMember(String name) {
        members.remove(name);
        memberNames.remove(name);
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

    public static void generateRandomList() {
        pointer = 0;
        Collections.shuffle(memberNames);
    }

    public static Member getRandomMember() {
        Member member = members.get(memberNames.get(pointer));
        pointer++;
        return member;
    }

    public static Boolean isLast(){
        System.out.println(pointer + " " + memberNames.size() + " " + members.size());
        return pointer < memberNames.size();
    }

    public static List<Member> getKRandomMember(int k, String targetNode) {
        List<Member> memberList = new ArrayList<>();
        // Check if map is not empty
        if (!memberNames.isEmpty()) {
            // Get a random key and the corresponding value
            while (memberList.size() < k && memberList.size() < memberNames.size()) {
                //TODO now send ping to other k nodes
                Member member = members.get(memberNames.get(ThreadLocalRandom.current().nextInt(memberNames.size())));
                if (!memberList.contains(member) && targetNode.equals(member.getName())) {
                    Member newMember = new Member(member.getName(),
                            member.getIpAddress(),
                            member.getPort(),
                            member.getVersionNo(),
                            member.getStatus(),
                            member.getDateTime());
                    memberList.add(newMember);
                }
            }
        } else {
            System.out.println("Map is empty.");
        }
        return memberList;
    }
}
