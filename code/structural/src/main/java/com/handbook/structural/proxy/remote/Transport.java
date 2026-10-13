package com.handbook.structural.proxy.remote;

/** Network abstraction: sends a request string, receives a response string (HTTP/gRPC in real life). */
@FunctionalInterface
public interface Transport {

    String send(String request) throws java.io.IOException;
}
