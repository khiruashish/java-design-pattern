package com.handbook.architecture.modular.shop.catalog;

import java.util.Optional;

/** Package-private: other modules cannot even name this class. */
class CatalogService implements CatalogApi {

    private final ProductTable table;

    CatalogService(ProductTable table) {
        this.table = table;
    }

    @Override
    public Optional<Long> priceOf(String sku) {
        return table.find(sku).filter(ProductTable.Row::active).map(ProductTable.Row::pricePaise);
    }
}
