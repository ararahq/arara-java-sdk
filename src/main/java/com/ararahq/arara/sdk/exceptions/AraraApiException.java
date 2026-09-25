package com.ararahq.arara.sdk.exceptions;

import com.ararahq.arara.sdk.models.AraraError;
import lombok.Getter;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * Thrown when the API returns an error (4xx or 5xx).
 * Carries the envelope {@code {"error": {"code", "message", "details"}}} returned by the server,
 * plus the {@code Retry-After} hint when the server sent one.
 */
@Getter
public class AraraApiException extends AraraException {
    private final int statusCode;
    private final AraraError errorDetails;
    private final Duration retryAfter;

    public AraraApiException(int statusCode, AraraError errorDetails) {
        this(statusCode, errorDetails, null);
    }

    public AraraApiException(int statusCode, AraraError errorDetails, Duration retryAfter) {
        super(resolveMessage(statusCode, errorDetails));
        this.statusCode = statusCode;
        this.errorDetails = errorDetails;
        this.retryAfter = retryAfter;
    }

    /**
     * @return The machine-readable error code (e.g. {@code INSUFFICIENT_FUNDS}), or null when absent.
     */
    public String getCode() {
        return errorDetails != null ? errorDetails.getCode() : null;
    }

    /**
     * @return The error details object, never null.
     */
    public Map<String, Object> getDetails() {
        if (errorDetails == null || errorDetails.getDetails() == null) {
            return Collections.emptyMap();
        }
        return errorDetails.getDetails();
    }

    private static String resolveMessage(int statusCode, AraraError errorDetails) {
        if (errorDetails != null && errorDetails.getMessage() != null) {
            return errorDetails.getMessage();
        }
        return "API Error " + statusCode;
    }
}
