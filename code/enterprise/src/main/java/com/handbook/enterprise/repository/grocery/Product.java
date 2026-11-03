package com.handbook.enterprise.repository.grocery;

/** Domain object. Knows nothing about SQL. */
public record Product(String sku, String name, String category, long pricePaise, int stock) {

    public Product withStock(int newStock) {
        return new Product(sku, name, category, pricePaise, newStock);
    }
}
