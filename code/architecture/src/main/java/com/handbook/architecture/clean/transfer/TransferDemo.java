package com.handbook.architecture.clean.transfer;

import com.handbook.architecture.clean.transfer.TransferFunds.Account;
import com.handbook.architecture.clean.transfer.TransferFunds.ApiPresenter;
import com.handbook.architecture.clean.transfer.TransferFunds.AuditPresenter;
import com.handbook.architecture.clean.transfer.TransferFunds.InMemoryAccounts;
import com.handbook.architecture.clean.transfer.TransferFunds.Interactor;
import com.handbook.architecture.clean.transfer.TransferFunds.Presenter;
import com.handbook.architecture.clean.transfer.TransferFunds.Request;
import com.handbook.architecture.clean.transfer.TransferFunds.Response;

public class TransferDemo {

    public static void main(String[] args) {
        var accounts = new InMemoryAccounts()
                .with(new Account("A-1", 10_000_000, 5_000_000))
                .with(new Account("B-2", 0, 5_000_000));
        var interactor = new Interactor(accounts);

        System.out.println("=== Banking: one interactor, a response model rendered twice ===");
        for (long paise : new long[] {2_000_000, 4_000_000, 20_000_000}) {
            ApiPresenter api = new ApiPresenter();
            AuditPresenter audit = new AuditPresenter();
            interactor.transfer(new Request("A-1", "B-2", paise, "app"), both(api, audit));
            System.out.println(api.output);
            System.out.println("  " + audit.output);
        }
    }

    private static Presenter both(Presenter a, Presenter b) {
        return (Response r) -> {
            a.present(r);
            b.present(r);
        };
    }
}
