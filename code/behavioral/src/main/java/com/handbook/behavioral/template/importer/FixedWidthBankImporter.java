package com.handbook.behavioral.template.importer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Bank statement file: fixed-width columns (account 10, date 8, amount 12, type 2). */
public class FixedWidthBankImporter extends DataImporter {

    protected String name() { return "Bank statement (fixed-width)"; }

    protected Map<String, String> parse(String line) {
        Map<String, String> r = new LinkedHashMap<>();
        r.put("account", line.substring(0, 10).strip());
        r.put("date", line.substring(10, 18));
        r.put("amountPaise", line.substring(18, 30).strip());
        r.put("type", line.substring(30, 32));
        return r;
    }

    protected Optional<String> validate(Map<String, String> r) {
        if (!r.get("type").equals("CR") && !r.get("type").equals("DR")) {
            return Optional.of("unknown type " + r.get("type"));
        }
        return r.get("amountPaise").matches("\\d+") ? Optional.empty() : Optional.of("bad amount");
    }

    @Override
    protected void onRejected(String line, String problem) { // bank files: rejections go to reconciliation
        System.out.println("  -> sent to reconciliation queue: " + problem);
    }
}
