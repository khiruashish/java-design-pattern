package com.handbook.behavioral.template.importer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Customers exported from a CRM as CSV with a header row. */
public class CsvCustomerImporter extends DataImporter {

    protected String name() { return "CRM customers (CSV)"; }

    @Override
    protected boolean skipFirstLine() { return true; } // header row

    protected Map<String, String> parse(String line) {
        String[] f = line.split(",", -1);
        Map<String, String> r = new LinkedHashMap<>();
        r.put("id", f[0]);
        r.put("name", f[1]);
        r.put("email", f[2]);
        return r;
    }

    protected Optional<String> validate(Map<String, String> r) {
        return r.get("email").contains("@") ? Optional.empty() : Optional.of("invalid email");
    }
}
