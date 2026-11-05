package com.handbook.microservices.healthcheck.storm;

/** Coding challenge: put the database in the LIVENESS probe and watch a 100s outage become a restart storm. */
public class RestartStormDemo {

    static final int PODS = 20;
    static final int STARTUP_SECONDS = 45; // JVM start + cache warm-up
    static final int FAILURE_THRESHOLD = 3; // probe every 10s, restart after 3 failures

    static String simulate(boolean databaseInLiveness) {
        int restarts = 0;
        int servingAtT120 = 0;
        int[] restartedAt = new int[PODS];
        java.util.Arrays.fill(restartedAt, -1_000);
        int[] failures = new int[PODS];
        for (int t = 0; t <= 120; t += 10) {
            boolean dbUp = t < 10 || t >= 110; // outage from t=10s to t=110s
            for (int p = 0; p < PODS; p++) {
                boolean starting = t - restartedAt[p] < STARTUP_SECONDS;
                boolean liveFails = databaseInLiveness && !dbUp;
                if (starting) {
                    continue;
                }
                failures[p] = liveFails ? failures[p] + 1 : 0;
                if (failures[p] >= FAILURE_THRESHOLD) {
                    restarts++;
                    restartedAt[p] = t;
                    failures[p] = 0;
                }
            }
        }
        for (int p = 0; p < PODS; p++) {
            if (120 - restartedAt[p] >= STARTUP_SECONDS) {
                servingAtT120++;
            }
        }
        return "restarts=" + restarts + ", pods able to serve at t=120s (DB back since t=110s): " + servingAtT120 + "/" + PODS;
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: The Database Belongs in Readiness, Not Liveness ===");
        System.out.println("DB in liveness : " + simulate(true));
        System.out.println("DB in readiness: " + simulate(false));
        System.out.println("Restarting can't fix a dependency; it only adds cold starts when the dependency returns.");
    }
}
