package com.handbook.behavioral.chain;

import com.handbook.behavioral.chain.checkout.CheckoutValidator;
import com.handbook.behavioral.chain.checkout.Order;
import com.handbook.behavioral.chain.checkout.Validators.*;
import com.handbook.behavioral.chain.loan.Approver;
import com.handbook.behavioral.chain.loan.Approvers.*;
import com.handbook.behavioral.chain.middleware.Middleware;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChainTest {

    @Test
    void firstFailingValidatorWins() {
        CheckoutValidator chain = new ServiceableArea();
        chain.then(new StockAvailable());
        assertThat(chain.validate(new Order("o", "999999", "NOPE", 99, null, 1))).hasValueSatisfying(
                m -> assertThat(m).contains("don't deliver"));
        assertThat(chain.validate(new Order("o", "560034", "MILK-1L", 1, null, 1))).isEmpty();
    }

    @Test
    void loanEscalatesToTheRightLevel() {
        Approver chain = new LoanOfficer();
        chain.escalatesTo(new BranchManager());
        assertThat(chain.decide("a", 100, 800)).startsWith("Loan Officer APPROVED");
        assertThat(chain.decide("a", 1_000_000, 800)).startsWith("Branch Manager APPROVED");
        assertThat(chain.decide("a", 50_000_000, 800)).contains("refer to the board");
    }

    @Test
    void middlewareRunsInOrderAndCanShortCircuit() {
        List<String> trace = new ArrayList<>();
        Middleware a = (r, n) -> { trace.add("a"); return n.proceed(r); };
        Middleware stop = (r, n) -> { trace.add("stop"); return new Middleware.Response(403, "no"); };
        Middleware never = (r, n) -> { trace.add("never"); return n.proceed(r); };
        var res = Middleware.chain(List.of(a, stop, never), r -> new Middleware.Response(200, "ok"))
                .proceed(new Middleware.Request("/", Map.of(), "ip"));
        assertThat(res.status()).isEqualTo(403);
        assertThat(trace).containsExactly("a", "stop");
    }
}
