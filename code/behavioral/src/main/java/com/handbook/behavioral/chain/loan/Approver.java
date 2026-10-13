package com.handbook.behavioral.chain.loan;

/** Handler: approves within its authority, otherwise escalates. */
public abstract class Approver {

    private final String title;
    private final long limitInr;
    private Approver escalateTo;

    protected Approver(String title, long limitInr) {
        this.title = title;
        this.limitInr = limitInr;
    }

    public Approver escalatesTo(Approver next) {
        this.escalateTo = next;
        return next;
    }

    public String decide(String applicant, long amountInr, int creditScore) {
        if (amountInr <= limitInr) {
            return title + " " + (approve(amountInr, creditScore) ? "APPROVED" : "DECLINED")
                    + " " + applicant + "'s loan of INR " + amountInr;
        }
        if (escalateTo == null) {
            return "No one can approve INR " + amountInr + "; refer to the board";
        }
        System.out.println("  " + title + " -> escalating INR " + amountInr);
        return escalateTo.decide(applicant, amountInr, creditScore);
    }

    protected abstract boolean approve(long amountInr, int creditScore);
}
