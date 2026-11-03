package com.handbook.microservices.competingconsumers.poison;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Coding challenge: one bad message must not block the queue or loop forever. */
public class PoisonMessageDemo {

    static final int MAX_RECEIVES = 3;

    public static void main(String[] args) {
        System.out.println("=== Challenge: Poison Messages and a Dead-Letter Queue ===");
        Deque<String> queue = new ArrayDeque<>(List.of("sms:+919845000001", "sms:NOT-A-NUMBER", "sms:+919845000002"));
        Map<String, Integer> receives = new HashMap<>();
        List<String> sent = new ArrayList<>();
        List<String> deadLetters = new ArrayList<>();

        while (!queue.isEmpty()) {
            String msg = queue.poll();
            int attempt = receives.merge(msg, 1, Integer::sum);
            try {
                String number = msg.substring(4);
                if (!number.matches("\\+91\\d{10}")) {
                    throw new IllegalArgumentException("invalid number " + number);
                }
                sent.add(number);
            } catch (IllegalArgumentException e) {
                if (attempt >= MAX_RECEIVES) {
                    deadLetters.add(msg + " (" + e.getMessage() + ", " + attempt + " attempts)"); // park it for humans
                } else {
                    queue.addLast(msg); // retry later, behind other work
                }
            }
        }
        System.out.println("Sent        : " + sent);
        System.out.println("Dead letters: " + deadLetters);
        System.out.println("Good messages weren't blocked, and the bad one stopped after " + MAX_RECEIVES + " tries.");
    }
}
