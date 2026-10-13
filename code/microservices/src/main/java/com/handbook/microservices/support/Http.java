package com.handbook.microservices.support;

import java.util.LinkedHashMap;
import java.util.Map;

/** Minimal HTTP request/response records so gateways, BFFs and sidecars can be shown without a web server. */
public final class Http {

    private Http() {
    }

    public record Request(String method, String path, Map<String, String> headers, String body) {
        public Request {
            headers = Map.copyOf(headers);
        }

        public static Request get(String path, Map<String, String> headers) {
            return new Request("GET", path, headers, "");
        }

        public String header(String name) {
            return headers.get(name);
        }

        public Request withPath(String newPath) {
            return new Request(method, newPath, headers, body);
        }

        public Request withHeader(String name, String value) {
            Map<String, String> h = new LinkedHashMap<>(headers);
            h.put(name, value);
            return new Request(method, path, h, body);
        }
    }

    public record Response(int status, Map<String, String> headers, String body) {
        public Response {
            headers = Map.copyOf(headers);
        }

        public static Response of(int status, String body) {
            return new Response(status, Map.of(), body);
        }

        public Response withHeader(String name, String value) {
            Map<String, String> h = new LinkedHashMap<>(headers);
            h.put(name, value);
            return new Response(status, h, body);
        }

        @Override
        public String toString() {
            return status + " " + body + (headers.isEmpty() ? "" : " " + new java.util.TreeMap<>(headers));
        }
    }
}
