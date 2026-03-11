package com.ararahq.arara.sdk.http;

import com.ararahq.arara.sdk.config.AraraConfig;
import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.exceptions.AraraAuthException;
import com.ararahq.arara.sdk.exceptions.AraraNetworkException;
import com.ararahq.arara.sdk.models.AraraError;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AraraHttpClient Tests")
class AraraHttpClientTest {

    private MockWebServer mockWebServer;
    private AraraHttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        AraraConfig config = AraraConfig.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .apiKey("test-key")
                .build();
        client = new AraraHttpClient(config);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
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

    static class TestResponse {
        public String name;

        public TestResponse() {
        }

        public TestResponse(String name) {
            this.name = name;
        }
    }
}
