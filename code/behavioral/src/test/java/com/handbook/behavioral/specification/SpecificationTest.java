package com.handbook.behavioral.specification;

import com.handbook.behavioral.specification.catalog.ProductSpecs;
import com.handbook.behavioral.specification.catalog.ProductSpecs.Product;
import com.handbook.behavioral.specification.core.Spec;
import com.handbook.behavioral.specification.courier.CourierSelection;
import com.handbook.behavioral.specification.loan.Eligibility;
import com.handbook.behavioral.specification.sql.SqlSpec;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SpecificationTest {

    private final Product p = new Product("x", "Pulse", 1_500, 4.0, 3, true);

    @Test
    void combinatorsAndDescriptions() {
        Spec<Product> s = ProductSpecs.inStock().and(ProductSpecs.brand("pulse").or(ProductSpecs.ratingAtLeast(4.5)));
        assertThat(s.isSatisfiedBy(p)).isTrue();
        assertThat(s.describe()).isEqualTo("(in stock AND (brand = pulse OR rating >= 4.5))");
        assertThat(ProductSpecs.inStock().not().isSatisfiedBy(p)).isFalse();
    }

    @Test
    void eligibilityListsEveryFailedRule() {
        var a = new Eligibility.Applicant("a", 19, 10_000, 600, 0, false);
        var r = Eligibility.check(a, Eligibility.PERSONAL_LOAN);
        assertThat(r.eligible()).isFalse();
        assertThat(r.reasons()).hasSize(4);
    }

    @Test
    void hazardousShipmentsNeedCertifiedCouriers() {
        var spec = CourierSelection.canCarry(new CourierSelection.Shipment(1, true, false, "1"));
        assertThat(spec.isSatisfiedBy(new CourierSelection.Courier("c", 10, false, true, Set.of("1")))).isFalse();
        assertThat(spec.isSatisfiedBy(new CourierSelection.Courier("c", 10, true, true, Set.of("1")))).isTrue();
    }

    @Test
    void sqlParamsStayInOrder() {
        SqlSpec s = SqlSpec.eq("a", 1).and(SqlSpec.eq("b", 2).or(SqlSpec.eq("c", 3)));
        assertThat(s.where()).isEqualTo("(a = ? AND (b = ? OR c = ?))");
        assertThat(s.params()).containsExactly(1, 2, 3);
    }
}
