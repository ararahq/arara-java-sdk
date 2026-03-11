package com.ararahq.arara.sdk.exceptions;

/**
 * Thrown when there are authentication issues (401, 403).
 */
public class AraraAuthException extends AraraException {
    public AraraAuthException(String message) {
        super(message);
    }
}
