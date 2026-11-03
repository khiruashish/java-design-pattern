package com.handbook.microservices.deployment.bluegreen;

import java.util.function.Function;

/** Two full environments; the router points ALL traffic at one of them. Switching (and rolling back) is one step. */
public final class BlueGreenRouter {

    private Function<String, String> blue;
    private Function<String, String> green;
    private String live = "blue";

    public BlueGreenRouter(Function<String, String> blue) {
        this.blue = blue;
    }

    public void deployToIdle(Function<String, String> newVersion) { // (1)!
        if (live.equals("blue")) {
            green = newVersion;
        } else {
            blue = newVersion;
        }
    }

    public String smokeTestIdle(String request) { // (2)!
        return (live.equals("blue") ? green : blue).apply(request);
    }

    public void switchTraffic() { // (3)!
        live = live.equals("blue") ? "green" : "blue";
    }

    public String handle(String request) {
        return live + ": " + (live.equals("blue") ? blue : green).apply(request);
    }
}
