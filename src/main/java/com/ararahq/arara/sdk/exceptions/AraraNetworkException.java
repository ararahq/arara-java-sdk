package com.ararahq.arara.sdk.exceptions;

/**
 * Thrown when network errors, timeouts, or connection issues occur.
 */
public class AraraNetworkException extends AraraException {
    public AraraNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
