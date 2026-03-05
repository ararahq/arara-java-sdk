package com.ararahq.arara.sdk.exceptions;

import com.ararahq.arara.sdk.models.AraraError;
import lombok.Getter;

/**
 * Thrown when the API returns an error (4xx or 5xx).
 * Contains structured error details returned by the server.
 */
@Getter
public class AraraApiException extends AraraException {
    private final int statusCode;
    private final AraraError errorDetails;

    public AraraApiException(int statusCode, AraraError errorDetails) {
        super(errorDetails != null ? errorDetails.getMessage() : "API Error " + statusCode);
        this.statusCode = statusCode;
        this.errorDetails = errorDetails;
    }
}
