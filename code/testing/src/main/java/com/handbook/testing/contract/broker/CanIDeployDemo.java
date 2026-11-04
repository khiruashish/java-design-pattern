package com.handbook.testing.contract.broker;

import com.handbook.testing.contract.broker.ContractBroker.Verification;

import java.util.List;

public class CanIDeployDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: 'Can I Deploy?' for the Accounts API ===");
        ContractBroker broker = new ContractBroker();
        broker.deployed("prod", "mobile-bff", "4.2.0");
        broker.deployed("prod", "statements", "2.7.1");
        broker.deployed("prod", "loans", "1.3.0");

        broker.record(new Verification("mobile-bff", "4.2.0", "accounts-api", "9.0.0", true));
        broker.record(new Verification("statements", "2.7.1", "accounts-api", "9.0.0", true));
        broker.record(new Verification("loans", "1.3.0", "accounts-api", "9.0.0", false)); // loans still reads 'branchCode'
        broker.record(new Verification("loans", "1.4.0", "accounts-api", "9.0.0", true));  // fixed, but not in prod yet

        List<String> consumers = List.of("mobile-bff", "statements", "loans");
        System.out.println("accounts-api 9.0.0 -> prod? " + broker.canIDeploy("accounts-api", "9.0.0", "prod", consumers));
        broker.deployed("prod", "loans", "1.4.0");
        System.out.println("after loans 1.4.0 ships -> prod? " + broker.canIDeploy("accounts-api", "9.0.0", "prod", consumers));
    }
}
