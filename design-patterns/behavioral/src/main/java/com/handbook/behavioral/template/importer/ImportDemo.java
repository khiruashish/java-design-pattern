package com.handbook.behavioral.template.importer;

import java.util.List;

public class ImportDemo {

    public static void main(String[] args) {
        System.out.println("=== ERP: Nightly Data Imports ===");
        System.out.println(new CsvCustomerImporter().run(List.of(
                "id,name,email",
                "C1,Asha Rao,asha@example.com",
                "C2,Ravi K,ravi-at-example.com",
                "C3,Li Wei,li@example.com")));

        System.out.println(new FixedWidthBankImporter().run(List.of(
                "SB0000100120261013000004500000CR",
                "SB0000100120261013000000129900DR",
                "SB0000100120261013000000050000XX")));
    }
}
