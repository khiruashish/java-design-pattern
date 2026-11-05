package com.handbook.behavioral.chain.loan;

import com.handbook.behavioral.chain.loan.Approvers.*;

public class LoanDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Loan Approval Escalation ===");
        Approver chain = new LoanOfficer();
        chain.escalatesTo(new BranchManager()).escalatesTo(new CreditCommittee());

        System.out.println(chain.decide("Meena", 300_000, 720));
        System.out.println(chain.decide("Rahul", 1_800_000, 710));
        System.out.println(chain.decide("Acme Foods Pvt Ltd", 15_000_000, 780));
        System.out.println(chain.decide("Mega Infra Ltd", 90_000_000, 800));
    }
}
