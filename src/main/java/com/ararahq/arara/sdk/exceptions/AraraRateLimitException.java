package com.ararahq.arara.sdk.exceptions;

import com.ararahq.arara.sdk.models.AraraError;

import java.time.Duration;

/**
 * Thrown when the API returns 429 Too Many Requests.
 * Exposes the Retry-After hint returned by the server, when present.
 */
public class AraraRateLimitException extends AraraApiException {
    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    public AraraRateLimitException(AraraError errorDetails, Duration retryAfter) {
        super(HTTP_TOO_MANY_REQUESTS, errorDetails, retryAfter);
    }
}
