package com.handbook.creational.prototype;

import com.handbook.creational.prototype.cart.Cart;
import com.handbook.creational.prototype.ecommerce.ProductListing;
import com.handbook.creational.prototype.healthcare.ReportTemplateRegistry;
import com.handbook.creational.prototype.saas.Board;
import com.handbook.creational.prototype.saas.Task;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrototypeTest {

    @Test
    void registryHandsOutIndependentCopies() {
        ReportTemplateRegistry registry = new ReportTemplateRegistry();
        String before = registry.template("CBC");
        var a = registry.newReport("CBC").forPatient("A").record("WBC", "7.0");
        var b = registry.newReport("CBC").forPatient("B");
        assertThat(a).isNotSameAs(b);
        assertThat(a.toString()).contains("WBC=7.0");
        assertThat(b.toString()).contains("WBC=pending");
        assertThat(registry.template("CBC")).isEqualTo(before);
        assertThatThrownBy(() -> registry.newReport("XRAY")).hasMessageContaining("Unknown panel");
    }

    @Test
    void variantsDoNotChangeTheBaseListing() {
        ProductListing base = new ProductListing("BASE", "Tee", "d", List.of("a.jpg"), Map.of("material", "cotton"), 100);
        String before = base.toString();
        ProductListing xl = base.variant("XL", "size", "XL", 120);
        assertThat(xl.toString()).contains("size=XL", "INR  120");
        assertThat(base.toString()).isEqualTo(before);
    }

    @Test
    void deepCopyIsIndependentButShallowCopyLeaks() {
        Board template = new Board("T").column("To Do", new Task("a", "PM"));
        template.copyAs("Deep").addTask("To Do", new Task("b", "Dev"));
        assertThat(template.taskCount()).isEqualTo(1);

        template.shallowCopyAs("Shallow").addTask("To Do", new Task("c", "Dev"));
        assertThat(template.taskCount()).isEqualTo(2); // documents the bug
    }

    @Test
    void cartCopyIsDeep() {
        Cart original = new Cart("C").add("MILK", 2);
        Cart copy = original.copy();
        copy.line(0).setQuantity(9);
        assertThat(original.toString()).isEqualTo("C [MILKx2]");
        assertThat(copy.toString()).isEqualTo("C [MILKx9]");
    }
}
