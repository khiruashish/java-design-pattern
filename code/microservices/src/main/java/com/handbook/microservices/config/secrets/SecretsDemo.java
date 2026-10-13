package com.handbook.microservices.config.secrets;

import java.util.Map;
import java.util.TreeMap;

public class SecretsDemo {

    public static void main(String[] args) {
        Map<String, String> fromGit = new TreeMap<>(Map.of(
                "db.url", "jdbc:postgresql://records-db:5432/records",
                "db.username", "records_svc",
                "db.password", "vault:secret/records/db#password",
                "lab.api.key", "vault:secret/records/lab#apiKey"));
        Map<String, String> vault = Map.of(
                "secret/records/db#password", "s3cr3t-rotated-2026-10",
                "secret/records/lab#apiKey", "lab-key-9f2c");

        var resolver = new SecretResolver(path -> {
            System.out.println("    vault read " + path + " (audited, short-lived token)");
            return vault.get(path);
        });

        System.out.println("=== Healthcare: secrets are references in Git, values only in memory ===");
        System.out.println("committed to Git : " + fromGit);
        Map<String, String> runtime = resolver.resolve(fromGit);
        System.out.println("logged at startup: " + SecretResolver.masked(runtime, fromGit));
        System.out.println("db.password usable by the DataSource: " + (runtime.get("db.password").length() > 0));
    }
}
