package com.ararahq.arara.sdk.utils;

import com.ararahq.arara.sdk.exceptions.AraraException;

/**
 * Utility methods for quick validations in the SDK.
 */
public final class ValidationUtils {
    private static final String WHATSAPP_PREFIX = "whatsapp:";
    private static final int MIN_PHONE_DIGITS = 7;
    private static final int MAX_PHONE_DIGITS = 15;

    private ValidationUtils() {
    }

    /**
     * Validates a recipient number with the same rule as the API: accepts
     * {@code whatsapp:+5511999998888}, {@code +5511999998888} or only digits.
     * The {@code whatsapp:} prefix is dropped, non-digits are ignored and 7 to 15 digits must remain.
     *
     * @param phoneNumber The number to be validated.
     * @throws AraraException if the number is invalid.
     */
    public static void validateWhatsAppNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new AraraException("Invalid phone number. Receiver is required.");
        }
        String withoutPrefix = phoneNumber.trim().startsWith(WHATSAPP_PREFIX)
                ? phoneNumber.trim().substring(WHATSAPP_PREFIX.length())
                : phoneNumber;
        long digits = withoutPrefix.chars().filter(Character::isDigit).count();
        if (digits < MIN_PHONE_DIGITS || digits > MAX_PHONE_DIGITS) {
            throw new AraraException("Invalid phone number '" + phoneNumber
                    + "'. Use E.164 digits, e.g. +5511999998888, 5511999998888 or whatsapp:+5511999998888.");
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
