package com.handbook.creational.objectpool.streaming;

/** Expensive worker: loads codecs and allocates GPU buffers on creation. */
public class Transcoder {

    private final int id;
    private int jobsDone;

    public Transcoder(int id) {
        this.id = id;
    }

    public void transcode(String video) throws InterruptedException {
        Thread.sleep(20); // stand-in for real encoding work
        jobsDone++;
    }

    public int id() { return id; }

    public int jobsDone() { return jobsDone; }
}
