package com.ararahq.arara.sdk.interceptors;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Interceptor that retries failed requests with exponential backoff.
 * Retries on network failures, 5xx responses and 429 responses,
 * honoring the Retry-After header when present.
 */
public class RetryInterceptor implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(RetryInterceptor.class);
    private static final long INITIAL_BACKOFF_MILLIS = 500L;
    private static final long MAX_BACKOFF_MILLIS = 8_000L;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    private final int maxRetries;

    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        for (int attempt = 0; ; attempt++) {
            Response response;
            try {
                response = chain.proceed(request);
            } catch (IOException e) {
                if (attempt >= maxRetries) {
                    throw e;
                }
                log.warn("Request failed, retrying. [url={}, attempt={}, reason={}]",
                        request.url(), attempt + 1, e.getMessage());
                sleep(backoffMillis(attempt));
                continue;
            }

            if (!isRetryable(response.code()) || attempt >= maxRetries) {
                return response;
            }

            long waitMillis = retryDelayMillis(response, attempt);
            log.warn("Received retryable status, retrying. [url={}, status={}, attempt={}, waitMillis={}]",
                    request.url(), response.code(), attempt + 1, waitMillis);
            response.close();
            sleep(waitMillis);
        }
    }

    /**
     * Parses a Retry-After header value in seconds or HTTP-date format.
     *
     * @param headerValue Raw header value, possibly null.
     * @return The wait duration, or null when absent or unparseable.
     */
    public static Duration parseRetryAfter(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        try {
            long seconds = Long.parseLong(headerValue.trim());
            return seconds >= 0 ? Duration.ofSeconds(seconds) : null;
        } catch (NumberFormatException e) {
            return parseHttpDate(headerValue.trim());
        }
    }

    private static Duration parseHttpDate(String headerValue) {
        try {
            ZonedDateTime date = ZonedDateTime.parse(headerValue, DateTimeFormatter.RFC_1123_DATE_TIME);
            Duration untilDate = Duration.between(ZonedDateTime.now(ZoneOffset.UTC), date);
            return untilDate.isNegative() ? Duration.ZERO : untilDate;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean isRetryable(int statusCode) {
        return statusCode == HTTP_TOO_MANY_REQUESTS || statusCode >= HTTP_INTERNAL_SERVER_ERROR;
    }

    private long retryDelayMillis(Response response, int attempt) {
        Duration retryAfter = parseRetryAfter(response.header("Retry-After"));
        return retryAfter != null ? retryAfter.toMillis() : backoffMillis(attempt);
    }

    private long backoffMillis(int attempt) {
        return Math.min(INITIAL_BACKOFF_MILLIS << attempt, MAX_BACKOFF_MILLIS);
    }

    private void sleep(long millis) throws IOException {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("Retry wait interrupted");
        }
    }
}
