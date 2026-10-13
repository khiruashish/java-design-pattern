package com.handbook.architecture.modular.shop.catalog;

import java.util.Optional;

/** The catalog module's ONLY public surface. Everything else in this package is package-private. */
public interface CatalogApi {

    Optional<Long> priceOf(String sku);

    static CatalogApi create() { // (1)!
        return new CatalogService(new ProductTable());
    }
}
