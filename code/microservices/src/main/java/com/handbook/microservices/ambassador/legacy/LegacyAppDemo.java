package com.handbook.microservices.ambassador.legacy;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;
import java.util.function.Function;

public class LegacyAppDemo {

    /** The 2009 loan-servicing app: plain HTTP to a configured base URL, no retries, no auth support. */
    static String legacyCheckEmi(Function<Request, Response> http) {
        Response r = http.apply(Request.get("/loans/LN-7781/emi", Map.of()));
        return r.status() + " " + r.body();
    }

    public static void main(String[] args) {
        System.out.println("=== Banking: An Ambassador Gives a Legacy App Auth and Retries ===");
        int[] calls = {0};
        Function<Request, Response> partner = req -> {
            calls[0]++;
            if (req.header("Authorization") == null) {
                return Response.of(401, "missing credentials");
            }
            return calls[0] < 3 ? Response.of(503, "busy") : Response.of(200, "EMI INR 18,450 due 05-Oct");
        };

        System.out.println("Legacy app -> partner directly : " + legacyCheckEmi(partner));
        calls[0] = 0;
        PartnerAmbassador ambassador = new PartnerAmbassador(partner, "k-live-91f2", 3);
        System.out.println("Legacy app -> localhost:9000   : " + legacyCheckEmi(ambassador));
        System.out.println("Ambassador log: " + ambassador.log);
        System.out.println("Legacy code unchanged: only its base URL config moved to http://localhost:9000.");
    }
}
