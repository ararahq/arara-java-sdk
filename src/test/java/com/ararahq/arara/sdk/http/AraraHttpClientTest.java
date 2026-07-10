package com.ararahq.arara.sdk.http;

import com.ararahq.arara.sdk.config.AraraConfig;
import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.exceptions.AraraAuthException;
import com.ararahq.arara.sdk.exceptions.AraraNetworkException;
import com.ararahq.arara.sdk.exceptions.AraraRateLimitException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AraraHttpClient Tests")
class AraraHttpClientTest {

    private MockWebServer mockWebServer;
    private AraraHttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        client = clientWithRetries(0);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private AraraHttpClient clientWithRetries(int maxRetries) {
        AraraConfig config = AraraConfig.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .apiKey("test-key")
                .maxRetries(maxRetries)
                .build();
        return new AraraHttpClient(config);
    }

    @Test
    @DisplayName("should handle successful GET")
    void shouldHandleSuccessfulGet() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Test\"}")
                .setResponseCode(200));

        TestResponse response = client.get("test", TestResponse.class);
        assertEquals("Test", response.name);
    }

    @Test
    @DisplayName("should handle successful POST")
    void shouldHandleSuccessfulPost() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Posted\"}")
                .setResponseCode(201));

        TestResponse response = client.post("test", new TestResponse("Data"), TestResponse.class);
        assertEquals("Posted", response.name);
    }

    @Test
    @DisplayName("should handle 401 Unauthorized")
    void shouldHandle401() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        assertThrows(AraraAuthException.class, () -> client.get("test", TestResponse.class));
    }

    @Test
    @DisplayName("should handle 400 Bad Request")
    void shouldHandle400() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"message\":\"Bad Request\"}")
                .setResponseCode(400));
        assertThrows(AraraApiException.class, () -> client.get("test", TestResponse.class));
    }

    @Test
    @DisplayName("should handle network failure")
    void shouldHandleNetworkFailure() throws IOException {
        mockWebServer.shutdown();
        assertThrows(AraraNetworkException.class, () -> client.get("test", TestResponse.class));
    }

    @Test
    @DisplayName("should throw AraraRateLimitException with retry after on 429")
    void shouldThrowRateLimitExceptionOn429() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(429)
                .setHeader("Retry-After", "7")
                .setBody("{\"error\":{\"code\":\"RATE_LIMITED\",\"message\":\"Too many requests\"}}"));

        AraraRateLimitException exception = assertThrows(AraraRateLimitException.class,
                () -> client.get("test", TestResponse.class));

        assertEquals(429, exception.getStatusCode());
        assertEquals(Duration.ofSeconds(7), exception.getRetryAfter());
        assertEquals("RATE_LIMITED", exception.getErrorDetails().getCode());
        assertEquals("Too many requests", exception.getErrorDetails().getMessage());
    }

    @Test
    @DisplayName("should throw AraraRateLimitException without retry after when header is absent")
    void shouldThrowRateLimitExceptionWithoutRetryAfter() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));

        AraraRateLimitException exception = assertThrows(AraraRateLimitException.class,
                () -> client.get("test", TestResponse.class));

        assertNull(exception.getRetryAfter());
    }

    @Test
    @DisplayName("should parse nested error envelope with details")
    void shouldParseNestedErrorEnvelope() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":{\"code\":\"INSUFFICIENT_CREDITS\","
                        + "\"message\":\"Not enough credits\","
                        + "\"details\":{\"balance\":0}}}"));

        AraraApiException exception = assertThrows(AraraApiException.class,
                () -> client.get("test", TestResponse.class));

        assertEquals("INSUFFICIENT_CREDITS", exception.getErrorDetails().getCode());
        assertEquals("Not enough credits", exception.getErrorDetails().getMessage());
        assertEquals(0, exception.getErrorDetails().getDetails().get("balance"));
    }

    @Test
    @DisplayName("should fall back to flat error format")
    void shouldFallBackToFlatErrorFormat() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"INVALID_INPUT\",\"message\":\"Invalid payload\"}"));

        AraraApiException exception = assertThrows(AraraApiException.class,
                () -> client.get("test", TestResponse.class));

        assertEquals("INVALID_INPUT", exception.getErrorDetails().getCode());
        assertEquals("Invalid payload", exception.getErrorDetails().getMessage());
    }

    @Test
    @DisplayName("should keep exception when error body is not valid JSON")
    void shouldHandleUnparseableErrorBody() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("upstream exploded"));

        AraraApiException exception = assertThrows(AraraApiException.class,
                () -> client.get("test", TestResponse.class));

        assertEquals(500, exception.getStatusCode());
        assertNull(exception.getErrorDetails());
    }

    @Test
    @DisplayName("should retry on 500 and succeed on next attempt")
    void shouldRetryOn500AndSucceed() throws InterruptedException {
        AraraHttpClient retryingClient = clientWithRetries(2);
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Recovered\"}")
                .setResponseCode(200));

        TestResponse response = retryingClient.get("test", TestResponse.class);

        assertEquals("Recovered", response.name);
        assertEquals(2, mockWebServer.getRequestCount());
        assertNotNull(mockWebServer.takeRequest());
        assertNotNull(mockWebServer.takeRequest());
    }

    @Test
    @DisplayName("should retry on 429 honoring Retry-After header")
    void shouldRetryOn429HonoringRetryAfter() {
        AraraHttpClient retryingClient = clientWithRetries(1);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(429)
                .setHeader("Retry-After", "0"));
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"AfterLimit\"}")
                .setResponseCode(200));

        TestResponse response = retryingClient.get("test", TestResponse.class);

        assertEquals("AfterLimit", response.name);
        assertEquals(2, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("should not retry when max retries is zero")
    void shouldNotRetryWhenMaxRetriesIsZero() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(AraraApiException.class, () -> client.get("test", TestResponse.class));
        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("should throw after exhausting retries")
    void shouldThrowAfterExhaustingRetries() {
        AraraHttpClient retryingClient = clientWithRetries(1);
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));

        AraraApiException exception = assertThrows(AraraApiException.class,
                () -> retryingClient.get("test", TestResponse.class));

        assertEquals(503, exception.getStatusCode());
        assertEquals(2, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("should not retry on 4xx other than 429")
    void shouldNotRetryOnClientError() {
        AraraHttpClient retryingClient = clientWithRetries(2);
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(AraraApiException.class, () -> retryingClient.get("test", TestResponse.class));
        assertEquals(1, mockWebServer.getRequestCount());
    }

    static class TestResponse {
        public String name;

        public TestResponse() {
        }

        public TestResponse(String name) {
            this.name = name;
        }
    }
}
