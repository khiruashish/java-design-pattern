package com.handbook.creational.abstractfactory.cloud;

/** Abstract factory: one method per product in the family. */
public interface CloudFactory {

    ObjectStorage objectStorage();

    MessageQueue messageQueue();
}
