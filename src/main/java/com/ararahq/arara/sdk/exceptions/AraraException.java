package com.ararahq.arara.sdk.exceptions;

import lombok.Getter;

/**
 * Base exception for the Arara SDK.
 */
@Getter
public class AraraException extends RuntimeException {
    public AraraException(String message) {
        super(message);
    }

    public AraraException(String message, Throwable cause) {
        super(message, cause);
    }
}
