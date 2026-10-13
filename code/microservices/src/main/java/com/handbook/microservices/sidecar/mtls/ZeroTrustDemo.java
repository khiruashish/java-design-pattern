package com.handbook.microservices.sidecar.mtls;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;
import java.util.Set;

public class ZeroTrustDemo {

    static final String CHECKOUT = "spiffe://shop.local/ns/prod/sa/checkout";
    static final String REPORTING = "spiffe://shop.local/ns/prod/sa/reporting";
    static final String PAYMENT = "spiffe://shop.local/ns/prod/sa/payment";

    public static void main(String[] args) {
        // The payment app only knows business logic.
        var paymentSidecar = new SidecarProxy(PAYMENT, Map.of(
                "/charges", Set.of(CHECKOUT),
                "/reports", Set.of(REPORTING, CHECKOUT)),
                r -> Response.of(200, "payment-app handled " + r.method() + " " + r.path()));

        var checkoutSidecar = new SidecarProxy(CHECKOUT, Map.of(), r -> Response.of(200, "checkout"));
        var reportingSidecar = new SidecarProxy(REPORTING, Map.of(), r -> Response.of(200, "reporting"));

        System.out.println("=== Grocery: zero-trust calls to payment-service through sidecars ===");
        Request charge = new Request("POST", "/charges", Map.of(), "{order: ORD-1}");
        System.out.println("checkout  -> POST /charges : " + paymentSidecar.inbound(checkoutSidecar.outbound(charge)));
        System.out.println("reporting -> POST /charges : " + paymentSidecar.inbound(reportingSidecar.outbound(charge)));
        System.out.println("reporting -> GET /reports  : "
                + paymentSidecar.inbound(reportingSidecar.outbound(Request.get("/reports/daily", Map.of()))));
        System.out.println("attacker pod (no cert)     : " + paymentSidecar.inbound(charge));
    }
}
