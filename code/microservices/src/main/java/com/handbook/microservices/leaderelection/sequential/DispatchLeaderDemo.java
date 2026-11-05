package com.handbook.microservices.leaderelection.sequential;

public class DispatchLeaderDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Electing the Route-Optimizer Leader with Sequential Nodes ===");
        SequentialElection election = new SequentialElection();
        for (String node : new String[] {"optimizer-a", "optimizer-b", "optimizer-c", "optimizer-d"}) {
            System.out.println(node + " created /election/n_" + String.format("%010d", election.join(node)));
        }
        System.out.println("Leader: " + election.leader().orElseThrow());

        election.sessionExpired("optimizer-c"); // a follower dies
        election.drainNotifications().forEach(n -> System.out.println("  " + n));
        election.sessionExpired("optimizer-a"); // the leader dies
        election.drainNotifications().forEach(n -> System.out.println("  " + n));
        System.out.println("Leader: " + election.leader().orElseThrow() + " (each death woke exactly one node: no herd effect)");
    }
}
