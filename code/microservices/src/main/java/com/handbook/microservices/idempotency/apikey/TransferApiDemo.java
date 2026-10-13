package com.handbook.microservices.idempotency.apikey;

import com.handbook.microservices.idempotency.apikey.IdempotencyKeyStore.StoredResponse;

import java.util.concurrent.atomic.AtomicInteger;

public class TransferApiDemo {

    public static void main(String[] args) {
        var store = new IdempotencyKeyStore();
        AtomicInteger transfersExecuted = new AtomicInteger();

        System.out.println("=== Banking: POST /transfers with an Idempotency-Key ===");
        String body = "{from: A-100, to: B-200, amount: 5000}";
        StoredResponse first = store.execute("key-7f3a", body,
                () -> new StoredResponse("", 201, "transfer TX-" + (1000 + transfersExecuted.incrementAndGet()) + " created"));
        System.out.println("1st attempt (response lost on a flaky mobile network): " + first.status() + " " + first.body());

        StoredResponse retry = store.execute("key-7f3a", body,
                () -> new StoredResponse("", 201, "transfer TX-" + (1000 + transfersExecuted.incrementAndGet()) + " created"));
        System.out.println("App retries with the same key                          : " + retry.status() + " " + retry.body());

        StoredResponse misuse = store.execute("key-7f3a", "{from: A-100, to: B-200, amount: 9000}",
                () -> new StoredResponse("", 201, "should not run"));
        System.out.println("Same key, different amount                             : " + misuse.status() + " " + misuse.body());
        System.out.println("Transfers actually executed: " + transfersExecuted.get());
    }
}
