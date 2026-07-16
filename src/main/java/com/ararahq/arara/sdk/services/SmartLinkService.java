package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CreateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.UpdateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.WhatsAppSmartLinkResponse;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Service for managing WhatsApp smart links.
 */
public class SmartLinkService {
    private static final String BASE = "/v1/smart-links/whatsapp";

    private final AraraHttpClient httpClient;

    public SmartLinkService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Creates a smart link. POST /v1/smart-links/whatsapp
     */
    public WhatsAppSmartLinkResponse create(CreateWhatsAppSmartLinkRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.post(BASE, request, WhatsAppSmartLinkResponse.class);
    }

    /**
     * Updates a smart link. PUT /v1/smart-links/whatsapp/{id}
     */
    public WhatsAppSmartLinkResponse update(String id, UpdateWhatsAppSmartLinkRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.put(BASE + "/" + id, request, WhatsAppSmartLinkResponse.class);
    }

    /**
     * Lists smart links. GET /v1/smart-links/whatsapp
     */
    public List<WhatsAppSmartLinkResponse> list() {
        return httpClient.get(BASE, new TypeReference<List<WhatsAppSmartLinkResponse>>() {
        });
    }

    /**
     * Returns click stats for a smart link. GET /v1/smart-links/whatsapp/{id}/stats
     */
    public Map<String, Object> stats(String id) {
        return httpClient.get(BASE + "/" + id + "/stats", new TypeReference<Map<String, Object>>() {
        });
    }
}
