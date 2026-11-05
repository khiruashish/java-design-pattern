package com.handbook.creational.abstractfactory.cloud;

public class AzureFactory implements CloudFactory {

    private final String account;

    public AzureFactory(String account) {
        this.account = account;
    }

    @Override
    public ObjectStorage objectStorage() {
        return (key, sizeKb) -> "https://%s.blob.core.windows.net/erp/%s (%d KB)"
                .formatted(account, key, sizeKb);
    }

    @Override
    public MessageQueue messageQueue() {
        return (topic, payload) -> "ServiceBus %s/%s <- %s".formatted(account, topic, payload);
    }
}
