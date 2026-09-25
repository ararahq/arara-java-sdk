package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.Arara;
import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.models.BatchMessageRequest;
import com.ararahq.arara.sdk.models.BatchMessageResponse;
import com.ararahq.arara.sdk.models.MessageResponse;
import com.ararahq.arara.sdk.models.SendMessageRequest;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MessageService against fake API")
class MessageServiceTest extends FakeApi {
    private static final String ACCEPTED = "{\"id\":\"msg_1\",\"status\":\"QUEUED\",\"mode\":\"LIVE\","
            + "\"receiver\":\"5511999998888\",\"cost\":0.05}";

    private SendMessageRequest template(String receiver) {
        return SendMessageRequest.builder()
                .receiver(receiver)
                .templateName("hello_world")
                .templateVariables(List.of("Ana"))
                .build();
    }

    @Test
    @DisplayName("should POST /v1/messages with body and parse the 202 response")
    void shouldPostMessageAndParseResponse() throws Exception {
        respond(202, ACCEPTED);

        MessageResponse response = arara.getMessages().send(template("whatsapp:+5511999998888"));

        RecordedRequest request = take("POST", "/v1/messages");
        JsonNode body = json(request);
        assertEquals("whatsapp:+5511999998888", body.get("receiver").asText());
        assertEquals("hello_world", body.get("templateName").asText());
        assertEquals("Ana", body.get("templateVariables").get(0).asText());
        assertEquals("msg_1", response.getId());
        assertEquals(new BigDecimal("0.05"), response.getCost());
    }

    @Test
    @DisplayName("should accept a response without cost")
    void shouldAcceptResponseWithoutCost() throws Exception {
        respond(202, "{\"id\":\"msg_2\",\"status\":\"SCHEDULED\"}");

        MessageResponse response = arara.getMessages().send(template("5511999998888"));

        take("POST", "/v1/messages");
        assertNull(response.getCost());
    }

    @Test
    @DisplayName("should always send a UUID v4 Idempotency-Key when caller gives none")
    void shouldGenerateIdempotencyKey() throws Exception {
        respond(202, ACCEPTED);

        arara.getMessages().send(template("+5511999998888"));

        String key = take("POST", "/v1/messages").getHeader("Idempotency-Key");
        assertNotNull(key);
        assertEquals(4, UUID.fromString(key).version());
    }

    @Test
    @DisplayName("should use the caller Idempotency-Key when given")
    void shouldUseCallerIdempotencyKey() throws Exception {
        respond(202, ACCEPTED);

        arara.getMessages().send(template("+5511999998888"), "order-42");

        assertEquals("order-42", take("POST", "/v1/messages").getHeader("Idempotency-Key"));
    }

    @Test
    @DisplayName("should retry after 5xx reusing the same Idempotency-Key")
    void shouldRetryWithSameKey() throws Exception {
        Arara retrying = client(2);
        respond(503, "{\"error\":{\"code\":\"SEND_TEMPORARILY_UNAVAILABLE\",\"message\":\"x\",\"details\":{}}}");
        respond(202, ACCEPTED);

        MessageResponse response = retrying.getMessages().send(template("+5511999998888"));

        String first = take("POST", "/v1/messages").getHeader("Idempotency-Key");
        String second = take("POST", "/v1/messages").getHeader("Idempotency-Key");
        assertEquals(first, second);
        assertEquals("msg_1", response.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"whatsapp:+5511999998888", "+5511999998888", "5511999998888"})
    @DisplayName("should accept every receiver format the API accepts")
    void shouldAcceptFlexibleReceiver(String receiver) throws Exception {
        respond(202, ACCEPTED);

        assertDoesNotThrow(() -> arara.getMessages().send(template(receiver)));

        assertEquals(receiver, json(take("POST", "/v1/messages")).get("receiver").asText());
    }

    @Test
    @DisplayName("should reject null request and invalid receiver without calling the API")
    void shouldRejectInvalidInputLocally() {
        assertThrows(AraraException.class, () -> arara.getMessages().send(null));
        assertThrows(AraraException.class, () -> arara.getMessages().send(template("abc")));
        assertEquals(0, server.getRequestCount());
    }

    @Test
    @DisplayName("should surface 422 INVALID_RECIPIENT with code")
    void shouldSurfaceInvalidRecipient() {
        respond(422, "{\"error\":{\"code\":\"INVALID_RECIPIENT\",\"message\":\"Numero invalido\",\"details\":{}}}");

        AraraApiException error = assertThrows(AraraApiException.class,
                () -> arara.getMessages().send(template("5511999998888")));

        assertEquals(422, error.getStatusCode());
        assertEquals("INVALID_RECIPIENT", error.getCode());
    }

    @Test
    @DisplayName("should GET /v1/messages/{id}")
    void shouldGetById() throws Exception {
        respond(200, ACCEPTED);

        MessageResponse response = arara.getMessages().getById("msg_1");

        take("GET", "/v1/messages/msg_1");
        assertEquals("QUEUED", response.getStatus());
    }

    @Test
    @DisplayName("should GET /v1/messages?batchId= as a raw list")
    void shouldListByBatch() throws Exception {
        respond(200, "[" + ACCEPTED + "]");

        List<MessageResponse> messages = arara.getMessages().listByBatch("b 1");

        take("GET", "/v1/messages?batchId=b+1");
        assertEquals(1, messages.size());
    }

    @Test
    @DisplayName("should POST /v1/messages/batch with Idempotency-Key and parse result")
    void shouldSendBatch() throws Exception {
        respond(202, "{\"batchId\":\"b1\",\"templateName\":\"hello\",\"total\":1,\"accepted\":1,"
                + "\"totalCost\":0.05,\"messages\":[{\"id\":\"m1\",\"receiver\":\"5511999998888\","
                + "\"status\":\"QUEUED\",\"cost\":0.05}]}");
        BatchMessageRequest request = BatchMessageRequest.builder()
                .templateName("hello")
                .messages(List.of(BatchMessageRequest.BatchMessageItem.builder()
                        .receiver("5511999998888")
                        .templateVariables(List.of("Ana"))
                        .build()))
                .build();

        BatchMessageResponse response = arara.getMessages().sendBatch(request);

        RecordedRequest recorded = take("POST", "/v1/messages/batch");
        assertNotNull(recorded.getHeader("Idempotency-Key"));
        assertEquals("hello", json(recorded).get("templateName").asText());
        assertEquals("b1", response.getBatchId());
        assertEquals("m1", response.getMessages().get(0).getId());
    }

    @Test
    @DisplayName("should reject a batch above 1000 items locally")
    void shouldRejectOversizedBatch() {
        List<BatchMessageRequest.BatchMessageItem> items = IntStream.range(0, 1001)
                .mapToObj(i -> BatchMessageRequest.BatchMessageItem.builder().receiver("5511999998888").build())
                .collect(Collectors.toList());
        BatchMessageRequest request = BatchMessageRequest.builder().templateName("t").messages(items).build();

        assertThrows(AraraException.class, () -> arara.getMessages().sendBatch(request, "k"));
        assertEquals(0, server.getRequestCount());
    }

    @Test
    @DisplayName("should trim the caller key and reject a blank one")
    void shouldTrimAndRejectBlankKey() throws Exception {
        respond(202, ACCEPTED);

        arara.getMessages().send(template("5511999998888"), "  order-7  ");

        assertEquals("order-7", take("POST", "/v1/messages").getHeader("Idempotency-Key"));
        assertThrows(AraraException.class, () -> arara.getMessages().send(template("5511999998888"), "   "));
        assertEquals(1, server.getRequestCount());
    }

    @Test
    @DisplayName("should raise NOT_FOUND on empty 403 from GET /v1/messages/{id}")
    void shouldMapEmpty403ToNotFound() throws Exception {
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(403));

        AraraApiException error = assertThrows(AraraApiException.class,
                () -> arara.getMessages().getById("msg_other"));

        take("GET", "/v1/messages/msg_other");
        assertFalse(error instanceof com.ararahq.arara.sdk.exceptions.AraraAuthException);
        assertEquals("NOT_FOUND", error.getCode());
        assertEquals(403, error.getStatusCode());
    }

    @Test
    @DisplayName("should keep 401 on GET /v1/messages/{id} as auth error")
    void shouldKeep401AsAuth() {
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(401));

        assertThrows(com.ararahq.arara.sdk.exceptions.AraraAuthException.class,
                () -> arara.getMessages().getById("msg_1"));
    }

    @Test
    @DisplayName("should reject a null batch item with its index")
    void shouldRejectNullBatchItem() {
        BatchMessageRequest request = BatchMessageRequest.builder().templateName("t")
                .messages(java.util.Arrays.asList(
                        BatchMessageRequest.BatchMessageItem.builder().receiver("5511999998888").build(), null))
                .build();

        AraraException error = assertThrows(AraraException.class, () -> arara.getMessages().sendBatch(request));

        assertTrue(error.getMessage().contains("messages[1]"));
        assertEquals(0, server.getRequestCount());
    }
}
