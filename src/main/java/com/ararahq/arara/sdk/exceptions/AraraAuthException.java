package com.ararahq.arara.sdk.exceptions;

import com.ararahq.arara.sdk.models.AraraError;

/**
 * Thrown when the API key is missing, invalid, expired or lacks permission:
 * any 401, or a 403 without an error code in the envelope.
 * A 403 carrying a business code (e.g. {@code PLAN_FEATURE_LOCKED}) is not an auth failure.
 */
public class AraraAuthException extends AraraApiException {
    public AraraAuthException(int statusCode, AraraError errorDetails) {
        super(statusCode, errorDetails);
    }
}
