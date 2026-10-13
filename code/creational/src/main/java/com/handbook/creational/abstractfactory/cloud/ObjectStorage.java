package com.handbook.creational.abstractfactory.cloud;

public interface ObjectStorage {

    /** Stores a file and returns its URI. */
    String upload(String key, int sizeKb);
}
