package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CreateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.PaginatedResponse;
import com.ararahq.arara.sdk.models.UpdateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.WhatsAppSmartLinkResponse;
import com.ararahq.arara.sdk.utils.QueryString;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

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
        ValidationUtils.checkNotNull(id, "id");
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.put(BASE + "/" + QueryString.encodePathSegment(id), request, WhatsAppSmartLinkResponse.class);
    }

    /**
     * Lists smart links. GET /v1/smart-links/whatsapp
     */
    public PaginatedResponse<WhatsAppSmartLinkResponse> list() {
        return list(null, null);
    }

    /**
     * Lists smart links with pagination. GET /v1/smart-links/whatsapp
     *
     * @param page Optional zero-based page.
     * @param size Optional page size.
     */
    public PaginatedResponse<WhatsAppSmartLinkResponse> list(Integer page, Integer size) {
        String path = QueryString.create().add("page", page).add("size", size).appendTo(BASE);
        return httpClient.get(path, new TypeReference<PaginatedResponse<WhatsAppSmartLinkResponse>>() {
        });
    }

    /**
     * Returns click stats for a smart link. GET /v1/smart-links/whatsapp/{id}/stats
     */
    public Map<String, Object> stats(String id) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get(BASE + "/" + QueryString.encodePathSegment(id) + "/stats", new TypeReference<Map<String, Object>>() {
        });
    }
}
