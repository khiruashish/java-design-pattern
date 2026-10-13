package com.handbook.microservices.saga.recovery;

import com.handbook.microservices.saga.recovery.DurableSaga.SagaLog;
import com.handbook.microservices.saga.recovery.DurableSaga.Step;

import java.util.List;

public class DurableSagaDemo {

    public static void main(String[] args) {
        SagaLog log = new SagaLog();
        List<Step> steps = List.of(
                new Step("reserve stock", () -> say("inventory: reserved"), () -> say("inventory: released")),
                new Step("charge payment", () -> say("payment: charged"), () -> say("payment: refunded")),
                new Step("schedule delivery", () -> {
                    throw new Error("orchestrator pod killed (OOM)"); // simulates a crash mid-saga
                }, () -> { }));

        System.out.println("=== Challenge: recovering a saga after the orchestrator crashes ===");
        try {
            new DurableSaga(log).run("ORD-7", steps);
        } catch (Error crash) {
            System.out.println("CRASH: " + crash.getMessage());
        }
        System.out.println("Saga log after crash : " + log.read("ORD-7"));

        System.out.println("-- new pod starts and scans unfinished sagas --");
        new DurableSaga(log).recover("ORD-7", steps);
        System.out.println("Saga log after restart: " + log.read("ORD-7"));
    }

    private static void say(String s) {
        System.out.println("  " + s);
    }
}
