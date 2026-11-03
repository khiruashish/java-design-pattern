package com.handbook.enterprise.repository.grocery;

import java.util.List;
import java.util.Optional;

/** The port: a collection-like interface in the language of the domain. */
public interface ProductRepository { // (1)!

    void save(Product product);

    Optional<Product> findBySku(String sku);

    List<Product> lowStock(int threshold); // (2)!
}
