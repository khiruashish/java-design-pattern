package com.handbook.microservices.bff.partner;

import com.handbook.microservices.support.Json;

import java.util.Map;
import java.util.Set;

/**
 * A BFF for external fintech partners. The internal account record has fields partners must never
 * see; the BFF owns the partner contract, masks data, and serves two contract versions side by side.
 */
public final class PartnerAccountBff {

    /** What the internal accounts service returns (full, internal view). */
    public record InternalAccount(String accountNumber, String holder, long balancePaise, String currency,
                                  String branchCode, int internalRiskScore, String kycStatus) {
    }

    public interface AccountService {
        InternalAccount find(String accountNumber);
    }

    private final AccountService accounts;
    private final Set<String> consentedAccounts;

    public PartnerAccountBff(AccountService accounts, Set<String> consentedAccounts) {
        this.accounts = accounts;
        this.consentedAccounts = consentedAccounts;
    }

    public Map<String, Object> account(String version, String accountNumber) {
        if (!consentedAccounts.contains(accountNumber)) { // (1)!
            return Json.obj("error", "NO_CONSENT");
        }
        InternalAccount a = accounts.find(accountNumber);
        String masked = "XXXX" + a.accountNumber().substring(a.accountNumber().length() - 4); // (2)!
        return switch (version) { // (3)!
            case "v1" -> Json.obj("account", masked, "balance", a.balancePaise() / 100.0);
            case "v2" -> Json.obj("account", masked,
                    "balance", Json.obj("amountMinor", a.balancePaise(), "currency", a.currency()),
                    "holderInitials", initials(a.holder()));
            default -> Json.obj("error", "UNSUPPORTED_VERSION");
        };
    }

    private static String initials(String name) {
        StringBuilder sb = new StringBuilder();
        for (String part : name.split(" ")) {
            sb.append(part.charAt(0)).append('.');
        }
        return sb.toString();
    }
}
