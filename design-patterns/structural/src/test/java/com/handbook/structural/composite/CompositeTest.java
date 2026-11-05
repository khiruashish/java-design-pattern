package com.handbook.structural.composite;

import com.handbook.structural.composite.banking.Permission;
import com.handbook.structural.composite.banking.Role;
import com.handbook.structural.composite.ecommerce.Bundle;
import com.handbook.structural.composite.ecommerce.Product;
import com.handbook.structural.composite.filesystem.Node;
import com.handbook.structural.composite.org.OrgUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompositeTest {

    @Test
    void bundlePriceAppliesDiscountsAtEachLevel() {
        Bundle inner = new Bundle("inner", 50).add(new Product("a", 100));
        Bundle outer = new Bundle("outer", 10).add(inner).add(new Product("b", 50));
        assertThat(inner.priceInr()).isEqualTo(50);
        assertThat(outer.priceInr()).isEqualTo(90);
    }

    @Test
    void emptyBundleCostsNothing() {
        assertThat(new Bundle("empty", 10).priceInr()).isZero();
    }

    @Test
    void departmentsRollUpHeadcountAndCost() {
        var team = new OrgUnit.Department("t").add(new OrgUnit.Employee("a", "x", 10), new OrgUnit.Employee("b", "y", 20));
        var org = new OrgUnit.Department("o").add(team, new OrgUnit.Employee("c", "z", 5));
        assertThat(org.headcount()).isEqualTo(3);
        assertThat(org.annualCostInr()).isEqualTo(35);
    }

    @Test
    void rolesInheritNestedPermissionsAndRejectCycles() {
        Role a = new Role("A").include(new Permission("p1"));
        Role b = new Role("B").include(a);
        Role c = new Role("C").include(b);
        assertThat(c.grants("p1")).isTrue();
        assertThat(c.grants("p2")).isFalse();
        assertThatThrownBy(() -> a.include(c)).hasMessageContaining("Cycle");
        assertThatThrownBy(() -> a.include(a)).hasMessageContaining("Cycle");
    }

    @Test
    void folderSizeIsSumOfDescendants() {
        var root = new Node.Folder("r", new Node.File("a", 1), new Node.Folder("s", new Node.File("b", 2)));
        assertThat(root.sizeBytes()).isEqualTo(3);
    }
}
