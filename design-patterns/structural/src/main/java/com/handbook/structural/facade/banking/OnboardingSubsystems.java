package com.handbook.structural.facade.banking;

/** Subsystems involved in opening a bank account. */
public final class OnboardingSubsystems {

    private OnboardingSubsystems() {
    }

    public static class KycService {
        public boolean verify(String pan, String aadhaarLast4) {
            return pan.matches("[A-Z]{5}[0-9]{4}[A-Z]") && aadhaarLast4.matches("\\d{4}");
        }
    }

    public static class CreditBureau {
        public int score(String pan) {
            return pan.startsWith("ABC") ? 790 : 640;
        }
    }

    public static class CoreBanking {
        private int next = 1001;

        public String openSavingsAccount(String name) {
            return "SB-" + next++;
        }
    }

    public static class CardService {
        public String issueDebitCard(String accountNo, boolean premium) {
            return (premium ? "Platinum" : "Classic") + " debit card for " + accountNo;
        }
    }
}
