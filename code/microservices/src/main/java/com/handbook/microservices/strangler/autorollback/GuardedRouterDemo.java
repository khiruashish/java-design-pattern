package com.handbook.microservices.strangler.autorollback;

public class GuardedRouterDemo {

    public static void main(String[] args) {
        var router = new GuardedRouter(10, 20);
        boolean[] modernBroken = {false};

        System.out.println("=== Challenge: automatic rollback when the new service misbehaves ===");
        StringBuilder served = new StringBuilder();
        for (int i = 1; i <= 30; i++) {
            if (i == 11) {
                modernBroken[0] = true; // a bad deploy of the new service
            }
            int request = i;
            String who = router.route(() -> {
                if (modernBroken[0] && request % 2 == 0) {
                    throw new IllegalStateException("500");
                }
                return "N";
            }, () -> "L");
            served.append(who);
            if (i % 10 == 0) {
                System.out.println("requests " + (i - 9) + "-" + i + ": " + served + "  (N=new, L=legacy)");
                served.setLength(0);
            }
        }
        System.out.println("Rolled back: " + router.rolledBack() + "; every request still got an answer.");
    }
}
