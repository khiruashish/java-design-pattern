package com.handbook.structural.flyweight.grocery;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class DarkStoreDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: 50 Products in 200 Dark Stores ===");
        Catalog catalog = new Catalog();
        List<StoreListing> listings = new ArrayList<>();
        for (int store = 1; store <= 200; store++) {
            for (int p = 1; p <= 50; p++) {
                int price = 40 + p + (store % 5); // prices vary slightly by store
                listings.add(new StoreListing("DS-" + store, catalog.product("SKU-" + p), price, (store * p) % 30));
            }
        }
        IdentityHashMap<ProductInfo, Boolean> distinct = new IdentityHashMap<>();
        listings.forEach(l -> distinct.put(l.product(), true));

        StoreListing a = listings.get(0);
        StoreListing b = listings.get(50); // same SKU-1, next store
        System.out.println(a.storeId() + " " + a.product().sku() + " INR " + a.priceInr() + " stock " + a.stock());
        System.out.println(b.storeId() + " " + b.product().sku() + " INR " + b.priceInr() + " stock " + b.stock());
        System.out.println("Shared ProductInfo instance? " + (a.product() == b.product()));
        System.out.println("Store listings      : " + listings.size());
        System.out.println("ProductInfo objects : " + distinct.size());
        System.out.println("Catalog loads       : " + catalog.loads());
    }
}
