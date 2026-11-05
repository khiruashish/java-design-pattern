package com.handbook.creational.abstractfactory.cloud;

public interface MessageQueue {

    /** Publishes a message and returns a delivery receipt. */
    String publish(String topic, String payload);
}
