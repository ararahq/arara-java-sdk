package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.MessageResponse;
import com.ararahq.arara.sdk.models.SendMessageRequest;
import com.ararahq.arara.sdk.utils.ValidationUtils;

/**
 * Service for managing WhatsApp messages.
 */
public class MessageService {
    private final AraraHttpClient httpClient;

    public MessageService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Sends a message (template or free text).
     *
     * @param request The send payload following API rules.
     * @return Details of the sent message, including ID and initial status.
     */
    public MessageResponse send(SendMessageRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        ValidationUtils.validateWhatsAppNumber(request.getReceiver());
        return httpClient.post("/v1/messages", request, MessageResponse.class);
    }

    /**
     * Retrieves message details by ID.
     *
     * @param id The unique message identifier.
     * @return Complete message details.
     */
    public MessageResponse getById(String id) {
        return httpClient.get("/v1/messages/" + id, MessageResponse.class);
    }
}
