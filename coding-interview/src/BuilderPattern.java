import java.time.Duration;
import java.util.*;

/** Problem: build an immutable HttpRequest with 2 required and many optional fields; validate in build(). */
public class BuilderPattern {

    record HttpRequest(String method, String url, Map<String, String> headers, String body, Duration timeout, int retries) {

        HttpRequest { headers = Map.copyOf(headers); }   // immutable result

        /** Required parameters go into the builder's entry point, so they can't be forgotten. */
        static Builder get(String url) { return new Builder("GET", url); }
        static Builder post(String url) { return new Builder("POST", url); }

        static final class Builder {
            private final String method;
            private final String url;
            private final Map<String, String> headers = new LinkedHashMap<>();
            private String body;
            private Duration timeout = Duration.ofSeconds(10);   // sensible defaults
            private int retries = 0;

            private Builder(String method, String url) {
                this.method = method;
                this.url = Objects.requireNonNull(url, "url");
            }

            Builder header(String name, String value) { headers.put(name, value); return this; }
            Builder body(String body) { this.body = body; return this; }
            Builder timeout(Duration timeout) { this.timeout = timeout; return this; }
            Builder retries(int retries) { this.retries = retries; return this; }

            /** Cross-field validation lives in ONE place, and an invalid object is never created. */
            HttpRequest build() {
                List<String> errors = new ArrayList<>();
                if (!url.startsWith("http://") && !url.startsWith("https://")) errors.add("url must be http(s)");
                if (method.equals("GET") && body != null) errors.add("GET must not have a body");
                if (method.equals("POST") && body == null) errors.add("POST needs a body");
                if (timeout.isNegative() || timeout.isZero()) errors.add("timeout must be > 0");
                if (retries < 0 || retries > 5) errors.add("retries must be 0..5");
                if (!errors.isEmpty()) throw new IllegalStateException(String.join("; ", errors));
                return new HttpRequest(method, url, headers, body, timeout, retries);
            }
        }
    }

    public static void main(String[] args) {
        HttpRequest get = HttpRequest.get("https://api.example.com/orders").header("Accept", "application/json").retries(2).build();
        HttpRequest post = HttpRequest.post("https://api.example.com/orders").body("{\"sku\":\"A1\"}").timeout(Duration.ofSeconds(3)).build();
        System.out.println(get);
        System.out.println(post);
        check(get.timeout().equals(Duration.ofSeconds(10)) && get.retries() == 2, "defaults + overrides");

        try {
            HttpRequest.get("ftp://x").body("oops").retries(9).build();
            check(false, "should fail");
        } catch (IllegalStateException e) {
            System.out.println("invalid request rejected: " + e.getMessage());
            check(e.getMessage().split(";").length == 3, "all three errors reported at once");
        }
        try { get.headers().put("X", "y"); check(false, "immutable"); }
        catch (UnsupportedOperationException e) { System.out.println("built object is immutable"); }
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
