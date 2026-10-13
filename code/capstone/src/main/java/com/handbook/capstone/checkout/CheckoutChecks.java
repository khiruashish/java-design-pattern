package com.handbook.capstone.checkout;

import com.handbook.capstone.cart.Cart;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * [Chain of Responsibility] Pre-checkout validations as a chain: each link either rejects the
 * request with a reason or passes it on. New checks are new links.
 */
public abstract class CheckoutChecks {

    public record Request(Cart cart, String pincode) {
    }

    private CheckoutChecks next;

    public CheckoutChecks then(CheckoutChecks next) {
        this.next = next;
        return next;
    }

    public final Optional<String> check(Request r) {
        Optional<String> problem = inspect(r);
        if (problem.isPresent() || next == null) {
            return problem;
        }
        return next.check(r);
    }

    protected abstract Optional<String> inspect(Request r);

    public static CheckoutChecks of(Predicate<Request> ok, String reason) {
        return new CheckoutChecks() {
            @Override
            protected Optional<String> inspect(Request r) {
                return ok.test(r) ? Optional.empty() : Optional.of(reason);
            }
        };
    }

    public static CheckoutChecks standardChain(Set<String> servedPincodes) {
        CheckoutChecks head = of(r -> r.cart().itemCount() > 0, "cart is empty");
        head.then(of(r -> r.cart().lines().values().stream().allMatch(l -> l.qty() <= l.product().maxPerOrder()),
                        "per-product limit exceeded"))
                .then(of(r -> servedPincodes.contains(r.pincode()), "we don't deliver to this pincode yet"));
        return head;
    }
}
