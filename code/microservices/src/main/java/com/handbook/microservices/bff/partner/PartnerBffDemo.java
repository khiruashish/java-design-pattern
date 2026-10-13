package com.handbook.microservices.bff.partner;

import com.handbook.microservices.bff.partner.PartnerAccountBff.InternalAccount;
import com.handbook.microservices.support.Json;

import java.util.Set;

public class PartnerBffDemo {

    public static void main(String[] args) {
        PartnerAccountBff.AccountService internal = number -> new InternalAccount(
                number, "Asha Rao", 1_254_075, "INR", "BLR-017", 12, "VERIFIED");

        var bff = new PartnerAccountBff(internal, Set.of("50100012345678"));

        System.out.println("=== Banking: partner BFF over the internal accounts service ===");
        System.out.println("Internal record   : " + internal.find("50100012345678"));
        System.out.println("GET /partner/v1/accounts/..5678 -> " + Json.of(bff.account("v1", "50100012345678")));
        System.out.println("GET /partner/v2/accounts/..5678 -> " + Json.of(bff.account("v2", "50100012345678")));
        System.out.println("GET /partner/v2/accounts/..9999 -> " + Json.of(bff.account("v2", "50100099999999")));
        System.out.println("Branch code, risk score and KYC status never leave the bank.");
    }
}
