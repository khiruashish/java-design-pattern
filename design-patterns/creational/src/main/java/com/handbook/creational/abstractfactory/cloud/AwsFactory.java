package com.handbook.creational.abstractfactory.cloud;

public class AwsFactory implements CloudFactory {

    private final String region;

    public AwsFactory(String region) {
        this.region = region;
    }

    @Override
    public ObjectStorage objectStorage() {
        return (key, sizeKb) -> "s3://erp-%s/%s (%d KB)".formatted(region, key, sizeKb);
    }

    @Override
    public MessageQueue messageQueue() {
        return (topic, payload) -> "SQS %s/%s <- %s".formatted(region, topic, payload);
    }
}
