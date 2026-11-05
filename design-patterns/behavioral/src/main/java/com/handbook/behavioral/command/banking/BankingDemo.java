package com.handbook.behavioral.command.banking;

import com.handbook.behavioral.command.banking.PaymentCommand.*;

public class BankingDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Nightly Batch of Scheduled Payments ===");
        Ledger ledger = new Ledger();
        CommandProcessor processor = new CommandProcessor(ledger);

        processor.submit(new Transfer("CMD-1", "ACC-1", "ACC-2", 2_000));
        processor.submit(new PayBill("CMD-2", "ACC-1", "ELECTRICITY-BESCOM", 1_450));
        processor.submit(new MobileRecharge("CMD-3", "ACC-2", "98450-00001", 3_000));
        processor.submit(new Transfer("CMD-1", "ACC-1", "ACC-2", 2_000)); // resent by a retrying client

        System.out.println("Running batch:");
        processor.runBatch();
        System.out.println("ACC-1 balance: INR " + ledger.balance("ACC-1"));
        System.out.println("ACC-2 balance: INR " + ledger.balance("ACC-2"));
        System.out.println("Dead letters : " + processor.deadLetters());
    }
}
