package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.Arara;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Base for service tests: runs the real SDK against a fake HTTP server.
 */
abstract class FakeApi {
    static final String API_KEY = "ara_live_test";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long TAKE_TIMEOUT_SECONDS = 2;

    MockWebServer server;
    Arara arara;

    @BeforeEach
    void startServer() throws IOException {
        server = new MockWebServer();
        server.start();
        arara = client(0);
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    Arara client(int maxRetries) {
        return Arara.builder()
                .apiKey(API_KEY)
                .baseUrl(server.url("/").toString())
                .maxRetries(maxRetries)
                .build();
    }

    void respond(int status, String body) {
        server.enqueue(new MockResponse()
                .setResponseCode(status)
                .setHeader("Content-Type", "application/json")
                .setBody(body));
    }

    RecordedRequest take(String method, String path) throws InterruptedException {
        RecordedRequest request = server.takeRequest(TAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertNotNull(request, "expected a request to " + method + " " + path);
        assertEquals(method, request.getMethod());
        assertEquals(path, request.getPath());
        assertEquals("Bearer " + API_KEY, request.getHeader("Authorization"));
        return request;
    }

    static JsonNode json(RecordedRequest request) throws IOException {
        return MAPPER.readTree(request.getBody().readUtf8());
    }
}
