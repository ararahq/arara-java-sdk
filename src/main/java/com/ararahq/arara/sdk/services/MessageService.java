package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.interceptors.RetryInterceptor;
import com.ararahq.arara.sdk.models.BatchMessageRequest;
import com.ararahq.arara.sdk.models.BatchMessageResponse;
import com.ararahq.arara.sdk.models.MessageResponse;
import com.ararahq.arara.sdk.models.SendMessageRequest;
import com.ararahq.arara.sdk.utils.QueryString;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for sending and reading WhatsApp messages.
 */
public class MessageService {
    private static final int MAX_BATCH_SIZE = 1000;
    private static final TypeReference<List<MessageResponse>> MESSAGE_LIST_TYPE =
            new TypeReference<List<MessageResponse>>() {
            };

    private final AraraHttpClient httpClient;

    public MessageService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Sends a message with an auto-generated Idempotency-Key (UUID v4), reused across retries.
     */
    public MessageResponse send(SendMessageRequest request) {
        return send(request, null);
    }

    /**
     * Sends a message (template or free text). POST /v1/messages
     *
     * @param request        The send payload.
     * @param idempotencyKey Caller key; when null a UUID v4 is generated. Reuse it to retry safely.
     * @return The accepted message (HTTP 202).
     */
    public MessageResponse send(SendMessageRequest request, String idempotencyKey) {
        ValidationUtils.checkNotNull(request, "request");
        ValidationUtils.validateWhatsAppNumber(request.getReceiver());
        return httpClient.post("/v1/messages", request, idempotencyHeaders(idempotencyKey), MessageResponse.class);
    }

    /**
     * Sends one template to up to 1000 recipients, with an auto-generated Idempotency-Key.
     */
    public BatchMessageResponse sendBatch(BatchMessageRequest request) {
        return sendBatch(request, null);
    }

    /**
     * Sends one template to up to 1000 recipients. POST /v1/messages/batch
     *
     * @param request        Template name and recipients.
     * @param idempotencyKey Caller key; when null a UUID v4 is generated.
     */
    public BatchMessageResponse sendBatch(
            BatchMessageRequest request, String idempotencyKey) {
        ValidationUtils.checkNotNull(request, "request");
        ValidationUtils.checkNotNull(request.getMessages(), "messages");
        if (request.getMessages().size() > MAX_BATCH_SIZE) {
            throw new AraraException(
                    "Batch accepts at most " + MAX_BATCH_SIZE + " messages.");
        }
        request.getMessages().forEach(item -> ValidationUtils.validateWhatsAppNumber(item.getReceiver()));
        return httpClient.post("/v1/messages/batch", request, idempotencyHeaders(idempotencyKey),
                BatchMessageResponse.class);
    }

    /**
     * Retrieves message details by ID. GET /v1/messages/{id}
     */
    public MessageResponse getById(String id) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get("/v1/messages/" + QueryString.encodePathSegment(id), MessageResponse.class);
    }

    /**
     * Lists the messages of a batch. GET /v1/messages?batchId=
     */
    public List<MessageResponse> listByBatch(String batchId) {
        ValidationUtils.checkNotNull(batchId, "batchId");
        return httpClient.get(QueryString.create().add("batchId", batchId).appendTo("/v1/messages"),
                MESSAGE_LIST_TYPE);
    }

    static Map<String, String> idempotencyHeaders(String idempotencyKey) {
        String key = idempotencyKey == null || idempotencyKey.isBlank()
                ? UUID.randomUUID().toString()
                : idempotencyKey;
        return Map.of(RetryInterceptor.IDEMPOTENCY_KEY_HEADER, key);
    }
}
