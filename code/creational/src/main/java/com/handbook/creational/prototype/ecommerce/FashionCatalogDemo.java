package com.handbook.creational.prototype.ecommerce;

import java.util.List;
import java.util.Map;

public class FashionCatalogDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: T-shirt Variants from One Base Listing ===");
        ProductListing base = new ProductListing("TSHIRT-BASE", "Organic Cotton Tee",
                "Soft, breathable, 180 GSM organic cotton.",
                List.of("front.jpg", "back.jpg", "fabric.jpg"),
                Map.of("material", "cotton"), 599);

        List<ProductListing> variants = List.of(
                base.variant("TSHIRT-S-BLK", "size", "S", 599),
                base.variant("TSHIRT-M-BLK", "size", "M", 599),
                base.variant("TSHIRT-XL-BLK", "size", "XL", 649));

        variants.forEach(System.out::println);
        System.out.println("Base listing unchanged: " + base);
    }
}
