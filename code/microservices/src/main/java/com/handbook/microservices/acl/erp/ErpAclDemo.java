package com.handbook.microservices.acl.erp;

import java.util.Map;
import java.util.TreeMap;

public class ErpAclDemo {

    public static void main(String[] args) {
        ErpProductTranslator.LegacyErp erp = materialNumber -> switch (materialNumber) {
            case "000000000000012345" -> Map.of("MAT_NO", "000000000000012345", "MAT_DESC", "ALPHONSO MANGO PREMIUM   ",
                    "BASE_UOM", "KGM", "PRICE", "1.299,00", "DEL_FLAG", "");
            case "000000000000012346" -> Map.of("MAT_NO", "000000000000012346", "MAT_DESC", "COCONUT WATER",
                    "BASE_UOM", "LTR", "PRICE", "89,50", "DEL_FLAG", "X");
            default -> Map.of();
        };
        var catalog = new ErpProductTranslator(erp);

        System.out.println("=== Grocery: reading products from a legacy ERP through an ACL ===");
        System.out.println("ERP says   : " + new TreeMap<>(erp.readMaterial("000000000000012345")));
        System.out.println("We see     : " + catalog.product("SKU-12345").orElseThrow());
        System.out.println("We see     : " + catalog.product("SKU-12346").orElseThrow());
        System.out.println("Unknown SKU: " + catalog.product("SKU-99999"));
    }
}
