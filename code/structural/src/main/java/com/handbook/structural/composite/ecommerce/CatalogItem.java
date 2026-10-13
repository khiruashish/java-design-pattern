package com.handbook.structural.composite.ecommerce;

/** Component: products and bundles are both catalog items. */
public sealed interface CatalogItem permits Product, Bundle {

    String name();

    int priceInr();

    String describe(String indent);
}
