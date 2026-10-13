package com.handbook.creational.abstractfactory.cloud;

import java.util.List;

/** Client. Depends only on the abstract factory and abstract products. */
public class InvoiceArchiver {

    private final ObjectStorage storage;
    private final MessageQueue queue;

    public InvoiceArchiver(CloudFactory cloud) { // (1)!
        this.storage = cloud.objectStorage();
        this.queue = cloud.messageQueue();
    }

    public List<String> archive(String invoiceId, int sizeKb) {
        String uri = storage.upload(invoiceId + ".pdf", sizeKb);
        String receipt = queue.publish("invoice-archived", invoiceId);
        return List.of(uri, receipt);
    }
}
