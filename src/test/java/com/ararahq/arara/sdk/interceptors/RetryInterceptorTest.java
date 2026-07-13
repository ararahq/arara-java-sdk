package com.ararahq.arara.sdk.interceptors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RetryInterceptor Tests")
class RetryInterceptorTest {

    @Test
    @DisplayName("should parse Retry-After in seconds")
    void shouldParseRetryAfterSeconds() {
        assertEquals(Duration.ofSeconds(30), RetryInterceptor.parseRetryAfter("30"));
    }

    @Test
    @DisplayName("should parse Retry-After with surrounding whitespace")
    void shouldParseRetryAfterWithWhitespace() {
        assertEquals(Duration.ofSeconds(5), RetryInterceptor.parseRetryAfter(" 5 "));
    }

    @Test
    @DisplayName("should return null for negative Retry-After")
    void shouldReturnNullForNegativeRetryAfter() {
        assertNull(RetryInterceptor.parseRetryAfter("-1"));
    }

    @Test
    @DisplayName("should return null for null or blank header")
    void shouldReturnNullForNullOrBlankHeader() {
        assertNull(RetryInterceptor.parseRetryAfter(null));
        assertNull(RetryInterceptor.parseRetryAfter(""));
        assertNull(RetryInterceptor.parseRetryAfter("   "));
    }

    @Test
    @DisplayName("should return null for unparseable header")
    void shouldReturnNullForUnparseableHeader() {
        assertNull(RetryInterceptor.parseRetryAfter("soon"));
    }

    @Test
    @DisplayName("should parse Retry-After in HTTP-date format")
    void shouldParseRetryAfterHttpDate() {
        String futureDate = DateTimeFormatter.RFC_1123_DATE_TIME
                .format(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(60));

        Duration parsed = RetryInterceptor.parseRetryAfter(futureDate);

        assertTrue(parsed.getSeconds() > 50 && parsed.getSeconds() <= 60);
    }

    @Test
    @DisplayName("should clamp past HTTP-date to zero")
    void shouldClampPastHttpDateToZero() {
        String pastDate = DateTimeFormatter.RFC_1123_DATE_TIME
                .format(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(60));

        assertEquals(Duration.ZERO, RetryInterceptor.parseRetryAfter(pastDate));
    }
}
