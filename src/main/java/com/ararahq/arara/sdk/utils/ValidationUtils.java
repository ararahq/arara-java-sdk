package com.ararahq.arara.sdk.utils;

import com.ararahq.arara.sdk.exceptions.AraraException;

/**
 * Utility methods for quick validations in the SDK.
 */
public class ValidationUtils {

    private ValidationUtils() {
        // Utility class should not be instantiated
    }

    /**
     * Validates if the phone number follows the expected Arara/WhatsApp pattern.
     *
     * @param phoneNumber The number to be validated.
     * @throws AraraException if the number is invalid.
     */
    public static void validateWhatsAppNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.startsWith("whatsapp:+")) {
            throw new AraraException("Invalid phone number. Must start with 'whatsapp:+'");
        }
    }

    /**
     * Checks if an object is null and throws a friendly error.
     */
    public static void checkNotNull(Object obj, String paramName) {
        if (obj == null) {
            throw new AraraException("Parameter '" + paramName + "' cannot be null.");
        }
    }
}
