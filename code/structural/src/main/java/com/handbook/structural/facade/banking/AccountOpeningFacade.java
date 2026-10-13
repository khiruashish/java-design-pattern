package com.handbook.structural.facade.banking;

import com.handbook.structural.facade.banking.OnboardingSubsystems.*;

/** Facade used by the mobile app, branch tablets and partner APIs alike. */
public class AccountOpeningFacade {

    public record Applicant(String name, String pan, String aadhaarLast4) {
    }

    private final KycService kyc = new KycService();
    private final CreditBureau bureau = new CreditBureau();
    private final CoreBanking core = new CoreBanking();
    private final CardService cards = new CardService();

    public String open(Applicant a) {
        if (!kyc.verify(a.pan(), a.aadhaarLast4())) {
            return a.name() + ": rejected, KYC failed";
        }
        int score = bureau.score(a.pan());
        String account = core.openSavingsAccount(a.name());
        String card = cards.issueDebitCard(account, score >= 750); // business rule lives in the facade
        return "%s: account %s opened, credit score %d, %s".formatted(a.name(), account, score, card);
    }
}
