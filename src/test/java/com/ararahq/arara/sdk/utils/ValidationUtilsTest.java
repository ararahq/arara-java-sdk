package com.ararahq.arara.sdk.utils;

import com.ararahq.arara.sdk.exceptions.AraraException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ValidationUtils and QueryString")
class ValidationUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "whatsapp:+5511999998888", "+5511999998888", "5511999998888",
            "+1 (201) 555-0123", "whatsapp:+447700900123", "1234567", "123456789012345"
    })
    @DisplayName("should accept every receiver format the API accepts")
    void shouldAcceptApiFormats(String phone) {
        assertDoesNotThrow(() -> ValidationUtils.validateWhatsAppNumber(phone));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "abc", "123456", "1234567890123456", "whatsapp:+", "tel:"})
    @DisplayName("should reject what the API rejects (fewer than 7 or more than 15 digits)")
    void shouldRejectInvalid(String phone) {
        AraraException error = assertThrows(AraraException.class,
                () -> ValidationUtils.validateWhatsAppNumber(phone));
        assertTrue(error.getMessage().startsWith("Invalid phone number"));
    }

    @Test
    @DisplayName("should include the parameter name when null")
    void shouldIncludeParameterName() {
        AraraException error = assertThrows(AraraException.class,
                () -> ValidationUtils.checkNotNull(null, "myParameter"));
        assertTrue(error.getMessage().contains("myParameter"));
        assertDoesNotThrow(() -> ValidationUtils.checkNotNull(0, "zero"));
    }

    @Test
    @DisplayName("should encode query values and skip nulls")
    void shouldBuildQueryString() {
        assertEquals("/p?a=1&q=a%26b+c",
                QueryString.create().add("a", 1).add("skip", null).add("q", "a&b c").appendTo("/p"));
        assertEquals("/p", QueryString.create().add("x", null).appendTo("/p"));
    }

    @Test
    @DisplayName("should encode path segments with plus and spaces")
    void shouldEncodePathSegment() {
        assertEquals("%2B5511999998888", QueryString.encodePathSegment("+5511999998888"));
        assertEquals("a%20b%2Fc", QueryString.encodePathSegment("a b/c"));
    }
}
