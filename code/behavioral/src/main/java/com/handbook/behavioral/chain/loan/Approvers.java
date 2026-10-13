package com.handbook.behavioral.chain.loan;

public final class Approvers {

    private Approvers() {
    }

    public static class LoanOfficer extends Approver {
        public LoanOfficer() { super("Loan Officer", 500_000); }

        protected boolean approve(long amount, int score) { return score >= 700; }
    }

    public static class BranchManager extends Approver {
        public BranchManager() { super("Branch Manager", 2_500_000); }

        protected boolean approve(long amount, int score) { return score >= 730; }
    }

    public static class CreditCommittee extends Approver {
        public CreditCommittee() { super("Credit Committee", 20_000_000); }

        protected boolean approve(long amount, int score) { return score >= 760; }
    }
}
