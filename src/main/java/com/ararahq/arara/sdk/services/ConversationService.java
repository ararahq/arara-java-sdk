package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.ConversationReplyRequest;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Service for managing inbox conversations.
 */
public class ConversationService {
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {
            };

    private final AraraHttpClient httpClient;

    public ConversationService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Lists conversations. GET /v1/conversations
     */
    public Map<String, Object> list(String status, String leadStatus, int page, int size) {
        StringBuilder path = new StringBuilder("/v1/conversations?page=").append(page).append("&size=").append(size);
        if (status != null) {
            path.append("&status=").append(status);
        }
        if (leadStatus != null) {
            path.append("&leadStatus=").append(leadStatus);
        }
        return httpClient.get(path.toString(), MAP_TYPE);
    }

    /**
     * Returns lead counts by status. GET /v1/conversations/lead-stats
     */
    public Map<String, Object> leadStats() {
        return httpClient.get("/v1/conversations/lead-stats", MAP_TYPE);
    }

    /**
     * Lists messages of a conversation. GET /v1/conversations/{conversationId}/messages
     */
    public Map<String, Object> messages(String conversationId, int page, int size) {
        return httpClient.get(
                "/v1/conversations/" + conversationId + "/messages?page=" + page + "&size=" + size, MAP_TYPE);
    }

    /**
     * Sends a free-text reply. POST /v1/conversations/reply
     */
    public Map<String, Object> reply(ConversationReplyRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.post("/v1/conversations/reply", request, MAP_TYPE);
    }

    /**
     * Updates a conversation status. PATCH /v1/conversations/{conversationId}/status
     */
    public Map<String, Object> updateStatus(String conversationId, String status) {
        return httpClient.patch(
                "/v1/conversations/" + conversationId + "/status", Map.of("status", status), MAP_TYPE);
    }

    /**
     * Checks 24h window status for phones. POST /v1/conversations/window-status
     */
    public Map<String, Object> windowStatus(List<String> phones) {
        ValidationUtils.checkNotNull(phones, "phones");
        return httpClient.post("/v1/conversations/window-status", Map.of("phones", phones), MAP_TYPE);
    }
}
