package com.handbook.behavioral.specification.sql;

import static com.handbook.behavioral.specification.sql.SqlSpec.*;

public class SqlDemo {

    public static void main(String[] args) {
        SqlSpec filter = gte("stock", 1).and(lte("price_inr", 2000))
                .and(eq("brand", "Pulse").or(gte("rating", 4.5)));
        System.out.println("SELECT * FROM products WHERE " + filter.where());
        System.out.println("params: " + filter.params());

        SqlSpec attack = eq("brand", "x' OR '1'='1");
        System.out.println("Injection attempt stays a parameter: " + attack.where() + " " + attack.params());
    }
}
