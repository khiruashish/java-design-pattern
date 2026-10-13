package com.handbook.structural.facade.banking;

import com.handbook.structural.facade.banking.AccountOpeningFacade.Applicant;

public class OnboardingDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Open an Account in One Call ===");
        AccountOpeningFacade onboarding = new AccountOpeningFacade();
        System.out.println(onboarding.open(new Applicant("Kavya", "ABCPK1234Q", "5678")));
        System.out.println(onboarding.open(new Applicant("Imran", "XYZPI9876L", "1234")));
        System.out.println(onboarding.open(new Applicant("Test User", "BAD-PAN", "12")));
    }
}
