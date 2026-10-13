package com.handbook.behavioral.command.offline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Invoker on the phone: sends immediately when online, queues when offline, replays in order. */
public class DriverApp {

    private final Consumer<DriverAction> server;
    private final List<DriverAction> outbox = new ArrayList<>();
    private boolean online = true;

    public DriverApp(Consumer<DriverAction> server) {
        this.server = server;
    }

    public void setOnline(boolean online) {
        this.online = online;
        System.out.println(online ? "[network] back online, replaying " + outbox.size() + " action(s)" : "[network] lost signal");
        if (online) {
            outbox.forEach(server); // (1)!
            outbox.clear();
        }
    }

    public void perform(DriverAction action) {
        if (online) {
            server.accept(action);
        } else {
            outbox.add(action);
            System.out.println("  queued offline: " + action.getClass().getSimpleName() + " at " + action.at());
        }
    }
}
