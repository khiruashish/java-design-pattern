package com.handbook.behavioral.mediator.chat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Mediator: routes messages, handles @mentions and mutes. Members only talk to the room. */
public class ChatRoom {

    private static final Pattern MENTION = Pattern.compile("@(\\w+)");

    private final String channel;
    private final Map<String, Member> members = new LinkedHashMap<>();
    private final Set<String> muted = new HashSet<>();

    public ChatRoom(String channel) {
        this.channel = channel;
    }

    public Member join(String name) {
        Member m = new Member(name, this);
        members.put(name, m);
        broadcast("system", name + " joined #" + channel);
        return m;
    }

    public void mute(String name) {
        muted.add(name);
    }

    void send(Member from, String text) {
        if (muted.contains(from.name())) {
            System.out.println("  [room] blocked message from muted member " + from.name());
            from.receive("system", "you are muted in #" + channel);
            return;
        }
        broadcast(from.name(), text);
        Matcher m = MENTION.matcher(text);
        while (m.find()) {
            Member target = members.get(m.group(1));
            if (target != null) {
                target.notify(from.name() + " mentioned you in #" + channel);
            }
        }
    }

    private void broadcast(String from, String text) {
        members.values().stream().filter(m -> !m.name().equals(from)).forEach(m -> m.receive(from, text));
    }

    public static class Member {
        private final String name;
        private final ChatRoom room;

        Member(String name, ChatRoom room) {
            this.name = name;
            this.room = room;
        }

        public String name() { return name; }

        public void say(String text) { room.send(this, text); }

        void receive(String from, String text) {
            if (name.equals("priya")) { // only print one member's view to keep the demo short
                System.out.println("  [priya's screen] " + from + ": " + text);
            }
        }

        void notify(String text) {
            System.out.println("  [push to " + name + "] " + text);
        }
    }
}
