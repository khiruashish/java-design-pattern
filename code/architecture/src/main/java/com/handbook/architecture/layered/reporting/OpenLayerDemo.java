package com.handbook.architecture.layered.reporting;

import com.handbook.architecture.layered.reporting.StatementLayers.StatementQueries;
import com.handbook.architecture.layered.reporting.StatementLayers.StatementService;

public class OpenLayerDemo {

    public static void main(String[] args) {
        StatementQueries queries = new StatementQueries();
        StatementService service = new StatementService(queries);

        System.out.println("=== Banking: statement report through closed vs. open layers ===");
        System.out.println("Closed layering (presentation -> service -> queries):");
        System.out.print(StatementLayers.render(() -> service.statement("ACC-1")));
        System.out.println("service methods that only forwarded: " + service.passThroughCalls);

        System.out.println("Open query layer (presentation -> queries, read-only):");
        System.out.print(StatementLayers.render(() -> queries.forAccount("ACC-1")));
        System.out.println("Same output, one less layer to maintain for a pure read.");
        System.out.println("Writes (transfers, payments) still go through the service layer and its rules.");
    }
}
