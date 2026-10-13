package com.handbook.architecture.modular.shop.catalog;

import java.util.Map;
import java.util.Optional;

/** Package-private: the catalog's own tables (its own schema in the shared database). */
class ProductTable {

    record Row(String sku, long pricePaise, boolean active) {
    }

    private final Map<String, Row> rows = Map.of(
            "MANGO-1KG", new Row("MANGO-1KG", 39_900, true),
            "RICE-5KG", new Row("RICE-5KG", 64_900, true),
            "OLD-SKU", new Row("OLD-SKU", 1_000, false));

    Optional<Row> find(String sku) {
        return Optional.ofNullable(rows.get(sku));
    }
}
