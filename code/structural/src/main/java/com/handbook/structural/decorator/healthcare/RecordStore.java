package com.handbook.structural.decorator.healthcare;

public interface RecordStore {

    void write(String key, String data);

    String read(String key);
}
