package com.handbook.principles.oop.abstraction;

public class AbstractionDemo {
    public static void main(String[] args) {
        String body = "Q3 revenue report. ".repeat(3);
        for (FileStorage storage : new FileStorage[]{new InMemoryStorage(), new CompressingStorage(new InMemoryStorage())}) {
            String readBack = new ReportArchiver(storage).archiveAndReadBack("q3.txt", body);
            System.out.println(storage.describe() + " -> round trip ok: " + readBack.equals(body));
        }
    }
}
