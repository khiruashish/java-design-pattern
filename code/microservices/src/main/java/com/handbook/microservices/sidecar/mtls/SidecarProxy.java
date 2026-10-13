package com.handbook.microservices.sidecar.mtls;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Runs next to the app (same pod) and intercepts all its traffic. Inbound, it checks the caller's
 * workload identity (from the mTLS certificate, simulated here by a header the mesh sets) against an
 * authorization policy. The app itself contains no security code.
 */
public final class SidecarProxy {

    private final String myIdentity;
    private final Map<String, Set<String>> allowedCallersByPath; // path prefix → SPIFFE IDs
    private final Function<Request, Response> app;

    public SidecarProxy(String myIdentity, Map<String, Set<String>> allowedCallersByPath,
                        Function<Request, Response> app) {
        this.myIdentity = myIdentity;
        this.allowedCallersByPath = allowedCallersByPath;
        this.app = app;
    }

    public Response inbound(Request request) {
        String peer = request.header("x-peer-spiffe-id"); // (1)!
        if (peer == null) {
            return Response.of(403, "rejected: no mTLS client certificate"); // (2)!
        }
        boolean allowed = allowedCallersByPath.entrySet().stream()
                .filter(e -> request.path().startsWith(e.getKey()))
                .anyMatch(e -> e.getValue().contains(peer)); // (3)!
        if (!allowed) {
            return Response.of(403, "RBAC: " + shortName(peer) + " may not call " + request.path());
        }
        return app.apply(request);
    }

    /** Outbound: the sidecar presents this workload's identity to the next hop. */
    public Request outbound(Request request) {
        return request.withHeader("x-peer-spiffe-id", myIdentity);
    }

    private static String shortName(String spiffeId) {
        return spiffeId.substring(spiffeId.lastIndexOf('/') + 1);
    }
}
