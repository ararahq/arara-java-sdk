package com.ararahq.arara.sdk.utils;

import com.ararahq.arara.sdk.exceptions.AraraException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ValidationUtils.
 */
@DisplayName("ValidationUtils Tests")
class ValidationUtilsTest {
    @Nested
    @DisplayName("validateWhatsAppNumber() method")
    class ValidateWhatsAppNumberTests {
        @Test
        @DisplayName("should accept valid WhatsApp number")
        void shouldAcceptValidWhatsAppNumber() {
            // Act & Assert - No exception should be thrown
            assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber("whatsapp:+5511999998888"));
        }

        @Test
        @DisplayName("should accept valid WhatsApp number with different country code")
        void shouldAcceptValidWhatsAppNumberWithDifferentCountryCode() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber("whatsapp:+15551234567"));
            assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber("whatsapp:+447700900123"));
        }

        @Test
        @DisplayName("should reject null number")
        void shouldRejectNullNumber() {
            // Act & Assert
            AraraException exception = assertThrows(AraraException.class,
                    () -> ValidationUtils.validateWhatsAppNumber(null));
            assertTrue(exception.getMessage().contains("whatsapp:+"));
        }

        @Test
        @DisplayName("should reject number without whatsapp prefix")
        void shouldRejectNumberWithoutWhatsappPrefix() {
            // Act & Assert
            assertThrows(AraraException.class,
                    () -> ValidationUtils.validateWhatsAppNumber("+5511999998888"));
        }

        @Test
        @DisplayName("should reject number with invalid prefix")
        void shouldRejectNumberWithInvalidPrefix() {
            // Act & Assert
            assertThrows(AraraException.class,
                    () -> ValidationUtils.validateWhatsAppNumber("telegram:+5511999998888"));
        }

        @Test
        @DisplayName("should reject empty string")
        void shouldRejectEmptyString() {
            // Act & Assert
            assertThrows(AraraException.class,
                    () -> ValidationUtils.validateWhatsAppNumber(""));
        }

        @Test
        @DisplayName("should reject number without plus sign")
        void shouldRejectNumberWithoutPlusSign() {
            // Act & Assert
            assertThrows(AraraException.class,
                    () -> ValidationUtils.validateWhatsAppNumber("whatsapp:5511999998888"));
        }

        @Test
        @DisplayName("should reject incomplete format")
        void shouldRejectIncompleteFormat() {
            // Act & Assert
            assertThrows(AraraException.class,
                    () -> ValidationUtils.validateWhatsAppNumber("whatsapp:"));
        }

        @Test
        @DisplayName("should accept valid Brazilian number")
        void shouldAcceptValidBrazilianNumber() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber("whatsapp:+5511999998888"));
            assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber("whatsapp:+5521987654321"));
        }

        @Test
        @DisplayName("should accept valid US number")
        void shouldAcceptValidUSNumber() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber("whatsapp:+12015550123"));
        }

        @Test
        @DisplayName("should validate multiple numbers")
        void shouldValidateMultipleNumbers() {
            // Arrange
            String[] validNumbers = {
                    "whatsapp:+5511999998888",
                    "whatsapp:+5521987654321",
                    "whatsapp:+15551234567",
                    "whatsapp:+447700900123"
            };
            // Act & Assert
            for (String number : validNumbers) {
                assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber(number));
            }
        }

        @Test
        @DisplayName("should reject various invalid formats")
        void shouldRejectVariousInvalidFormats() {
            // Arrange
            String[] invalidNumbers = {
                    "+5511999998888", // Missing prefix
                    "whatsapp5511999998888", // Missing + and :
                    "whatsapp:5511999998888", // Missing +
                    "5511999998888", // No prefix or +
                    "tel:+5511999998888", // Wrong prefix
                    null // Null value
            };
            // Act & Assert
            for (String number : invalidNumbers) {
                assertThrows(AraraException.class,
                        () -> ValidationUtils.validateWhatsAppNumber(number),
                        "Should reject: " + number);
            }
        }
    }

    @Nested
    @DisplayName("checkNotNull() method")
    class CheckNotNullTests {
        @Test
        @DisplayName("should accept non-null object")
        void shouldAcceptNonNullObject() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull("test", "param"));
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(new Object(), "param"));
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(123, "param"));
        }

        @Test
        @DisplayName("should reject null object")
        void shouldRejectNullObject() {
            // Act & Assert
            assertThrows(AraraException.class,
                    () -> ValidationUtils.checkNotNull(null, "testParam"));
        }

        @Test
        @DisplayName("should include parameter name in error message")
        void shouldIncludeParameterNameInErrorMessage() {
            // Act
            AraraException exception = assertThrows(AraraException.class,
                    () -> ValidationUtils.checkNotNull(null, "myParameter"));
            // Assert
            assertTrue(exception.getMessage().contains("myParameter"));
        }

        @Test
        @DisplayName("should handle various parameter names")
        void shouldHandleVariousParameterNames() {
            // Arrange
            String[] paramNames = { "request", "id", "name", "email", "config" };
            // Act & Assert
            for (String paramName : paramNames) {
                AraraException exception = assertThrows(AraraException.class,
                        () -> ValidationUtils.checkNotNull(null, paramName));
                assertTrue(exception.getMessage().contains(paramName));
            }
        }

        @Test
        @DisplayName("should accept zero value")
        void shouldAcceptZeroValue() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(0, "zeroValue"));
        }

        @Test
        @DisplayName("should accept false value")
        void shouldAcceptFalseValue() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(false, "falseValue"));
        }

        @Test
        @DisplayName("should accept empty string")
        void shouldAcceptEmptyString() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull("", "emptyString"));
        }

        @Test
        @DisplayName("should accept empty collection")
        void shouldAcceptEmptyCollection() {
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(java.util.Collections.emptyList(), "emptyList"));
        }

        @Test
        @DisplayName("should validate multiple objects")
        void shouldValidateMultipleObjects() {
            // Arrange
            Object obj1 = new Object();
            Object obj2 = "test";
            Object obj3 = 123;
            // Act & Assert
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(obj1, "obj1"));
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(obj2, "obj2"));
            assertDoesNotThrow(() -> ValidationUtils.checkNotNull(obj3, "obj3"));
        }
    }

    @Nested
    @DisplayName("Combined validation scenarios")
    class CombinedValidationScenarios {
        @Test
        @DisplayName("should validate request object and phone number together")
        void shouldValidateRequestObjectAndPhoneNumberTogether() {
            // Arrange
            Object request = new Object();
            String phoneNumber = "whatsapp:+5511999998888";
            // Act & Assert
            assertDoesNotThrow(() -> {
                ValidationUtils.checkNotNull(request, "request");
                ValidationUtils.validateWhatsAppNumber(phoneNumber);
            });
        }

        @Test
        @DisplayName("should fail fast on null request")
        void shouldFailFastOnNullRequest() {
            // Arrange
            String phoneNumber = "whatsapp:+5511999998888";
            // Act & Assert
            assertThrows(AraraException.class, () -> {
                ValidationUtils.checkNotNull(null, "request");
                ValidationUtils.validateWhatsAppNumber(phoneNumber);
            });
        }

        @Test
        @DisplayName("should fail on invalid phone number even with valid request")
        void shouldFailOnInvalidPhoneNumberEvenWithValidRequest() {
            // Arrange
            Object request = new Object();
            String phoneNumber = "+5511999998888"; // Missing prefix
            // Act & Assert
            assertThrows(AraraException.class, () -> {
                ValidationUtils.checkNotNull(request, "request");
                ValidationUtils.validateWhatsAppNumber(phoneNumber);
            });
        }

        @Test
        @DisplayName("should validate multiple phone numbers in sequence")
        void shouldValidateMultiplePhoneNumbersInSequence() {
            // Arrange
            String[] phones = {
                    "whatsapp:+5511999998888",
                    "whatsapp:+5521987654321",
                    "whatsapp:+15551234567"
            };
            // Act & Assert
            for (String phone : phones) {
                assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber(phone));
            }
        }
    }
}
