package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CampaignPage;
import com.ararahq.arara.sdk.models.CampaignRequest;
import com.ararahq.arara.sdk.models.CampaignResponse;
import com.ararahq.arara.sdk.utils.QueryString;
import com.ararahq.arara.sdk.utils.ValidationUtils;

import java.util.UUID;

/**
 * Service for managing bulk message campaigns.
 */
public class CampaignService {
    private static final String BASE = "/v1/campaigns";

    private final AraraHttpClient httpClient;

    public CampaignService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Creates a campaign with an auto-generated Idempotency-Key (UUID v4), reused across retries.
     */
    public CampaignResponse create(CampaignRequest request) {
        return create(request, null);
    }

    /**
     * Creates and starts a new message campaign. POST /v1/campaigns
     *
     * @param request        Campaign data and contact list.
     * @param idempotencyKey Caller key; when null a UUID v4 is generated.
     */
    public CampaignResponse create(CampaignRequest request, String idempotencyKey) {
        ValidationUtils.checkNotNull(request, "request");
        ValidationUtils.checkNotNull(request.getContacts(), "contacts");
        request.getContacts().forEach(contact -> ValidationUtils.validateWhatsAppNumber(contact.getTo()));
        return httpClient.post(BASE, request, MessageService.idempotencyHeaders(idempotencyKey),
                CampaignResponse.class);
    }

    /**
     * Lists campaigns. GET /v1/campaigns
     *
     * @param page   Zero-based page.
     * @param size   Page size.
     * @param status Optional status filter.
     */
    public CampaignPage list(int page, int size, String status) {
        String path = QueryString.create()
                .add("page", page)
                .add("size", size)
                .add("status", status)
                .appendTo(BASE);
        return httpClient.get(path, CampaignPage.class);
    }

    /**
     * Retrieves campaign details by ID. GET /v1/campaigns/{id}
     */
    public CampaignResponse getById(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get(BASE + "/" + id, CampaignResponse.class);
    }

    /**
     * Cancels a scheduled or running campaign. POST /v1/campaigns/{id}/cancel
     */
    public void cancel(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        httpClient.post(BASE + "/" + id + "/cancel", null, Void.class);
    }
}
