package com.handbook.principles;

import com.handbook.principles.solid.dip.after.MessageSender;
import com.handbook.principles.solid.dip.after.OrderNotifier;
import com.handbook.principles.solid.isp.after.BasicPrinter;
import com.handbook.principles.solid.isp.after.MultiFunctionDevice;
import com.handbook.principles.solid.isp.after.PrintJob;
import com.handbook.principles.solid.isp.after.Scanner;
import com.handbook.principles.solid.lsp.before.Resizer;
import com.handbook.principles.solid.ocp.after.AreaCalculator;
import com.handbook.principles.solid.ocp.after.Circle;
import com.handbook.principles.solid.ocp.after.Rectangle;
import com.handbook.principles.solid.ocp.after.Shape;
import com.handbook.principles.solid.ocp.after.Triangle;
import com.handbook.principles.solid.srp.LineItem;
import com.handbook.principles.solid.srp.after.CsvInvoicePrinter;
import com.handbook.principles.solid.srp.after.Invoice;
import com.handbook.principles.solid.srp.after.InvoiceRepository;
import com.handbook.principles.solid.srp.after.TaxCalculator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class SolidPrinciplesTest {

    private final Invoice invoice = new Invoice("INV-1", List.of(new LineItem("Keyboard", 1, 4500), new LineItem("Mouse", 2, 1250)));

    @Test
    void srp_taxRuleChangesWithoutTouchingPrintingOrStorage() {
        assertThat(invoice.subtotalCents()).isEqualTo(7000);
        assertThat(new TaxCalculator(18).totalWithTaxCents(invoice)).isEqualTo(8260);
        assertThat(new TaxCalculator(5).totalWithTaxCents(invoice)).isEqualTo(7350);

        String csv = new CsvInvoicePrinter().print(invoice, 8260);
        assertThat(csv).startsWith("number,description,quantity,line_total_cents").endsWith(",TOTAL,,8260");

        InvoiceRepository repo = new InvoiceRepository();
        repo.save(invoice);
        assertThat(repo.find("INV-1")).contains(invoice);
    }

    @Test
    void ocp_newShapeNeedsNoCalculatorChange() {
        List<Shape> shapes = List.of(new Circle(1), new Rectangle(2, 3), new Triangle(4, 5));
        assertThat(new AreaCalculator().totalArea(shapes)).isCloseTo(Math.PI + 6 + 10, within(1e-9));
    }

    @Test
    void ocp_beforeVersionRejectsUnknownShapes() {
        var before = new com.handbook.principles.solid.ocp.before.AreaCalculator();
        assertThatThrownBy(() -> before.totalArea(List.<Object>of("not a shape"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lsp_squareBreaksRectangleContract() {
        Resizer resizer = new Resizer();
        assertThat(resizer.resizeTo5x4(new com.handbook.principles.solid.lsp.before.Rectangle())).isEqualTo(20);
        assertThat(resizer.resizeTo5x4(new com.handbook.principles.solid.lsp.before.Square())).isEqualTo(16);
    }

    @Test
    void lsp_immutableShapesAreSubstitutable() {
        List<com.handbook.principles.solid.lsp.after.Shape> shapes =
                List.of(new com.handbook.principles.solid.lsp.after.Rectangle(5, 4), new com.handbook.principles.solid.lsp.after.Square(4));
        assertThat(shapes).extracting(com.handbook.principles.solid.lsp.after.Shape::area).containsExactly(20, 16);
    }

    @Test
    void isp_clientsDependOnlyOnTheRoleTheyUse() {
        assertThat(new PrintJob().run(new BasicPrinter(), List.of("a", "b"))).isEqualTo("printed a, printed b");
        Scanner scanner = new MultiFunctionDevice();
        assertThat(scanner.scan("x")).isEqualTo("scanned x");
        assertThat(new BasicPrinter()).isNotInstanceOf(Scanner.class);
    }

    @Test
    void isp_fatInterfaceFailsAtRuntime() {
        var machine = new com.handbook.principles.solid.isp.before.BasicPrinter();
        assertThatThrownBy(() -> machine.fax("doc", "123")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void dip_highLevelPolicyTestedWithAFake() {
        List<String> sent = new ArrayList<>();
        MessageSender fake = (to, text) -> { sent.add(to + ":" + text); return "ok"; };
        new OrderNotifier(fake).orderShipped("bob", "A-1");
        assertThat(sent).containsExactly("bob:Order A-1 has shipped");
    }
}
