package com.handbook.behavioral.visitor;

import com.handbook.behavioral.visitor.cart.CartItems.*;
import com.handbook.behavioral.visitor.cart.GstVisitor;
import com.handbook.behavioral.visitor.cart.ShippingVisitor;
import com.handbook.behavioral.visitor.claims.Claim;
import com.handbook.behavioral.visitor.claims.ClaimOperations;
import com.handbook.behavioral.visitor.docs.Doc;
import com.handbook.behavioral.visitor.docs.Exporters;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisitorTest {

    @Test
    void gstOnlyOnElectronics() {
        GstVisitor v = new GstVisitor();
        new Book("b", 100, 1).accept(v);
        new Electronics("e", 100, 1, false).accept(v);
        assertThat(v.total()).isEqualTo(18.0);
    }

    @Test
    void shippingAddsSurcharges() {
        ShippingVisitor v = new ShippingVisitor();
        new Electronics("e", 1, 500, true).accept(v);
        assertThat(v.costInr()).isEqualTo(70);
    }

    @Test
    void claimRulesWithGuards() {
        assertThat(ClaimOperations.decide(new Claim.Surgery("x", 500_000, true, 3))).isEqualTo("APPROVE 300000 (sum insured cap)");
        assertThat(ClaimOperations.decide(new Claim.Pharmacy("x", 10, false))).startsWith("REJECT");
    }

    @Test
    void wordCountIgnoresImages() {
        List<Doc.Element> doc = List.of(new Doc.Heading(1, "Two words"), new Doc.Image("a", "many alt words here"));
        assertThat(doc.stream().mapToInt(e -> e.accept(new Exporters.WordCount())).sum()).isEqualTo(2);
    }
}
