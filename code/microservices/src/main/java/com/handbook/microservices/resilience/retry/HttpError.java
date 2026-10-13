package com.handbook.microservices.resilience.retry;

/** An HTTP failure with its status code, so retry policies can tell transient from permanent errors. */
public final class HttpError extends RuntimeException {

    private final int status;

    public HttpError(int status, String message) {
        super(status + " " + message);
        this.status = status;
    }

    public int status() {
        return status;
    }

    /** 429, 502, 503 and 504 are usually transient; 4xx client errors are not. */
    public static boolean isTransient(RuntimeException e) {
        return e instanceof HttpError h && (h.status == 429 || h.status == 502 || h.status == 503 || h.status == 504);
    }
}
