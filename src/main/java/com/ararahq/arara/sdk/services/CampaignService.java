package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CampaignRequest;
import com.ararahq.arara.sdk.models.CampaignResponse;

import java.util.UUID;

/**
 * Service for managing bulk message campaigns.
 */
public class CampaignService {
    private final AraraHttpClient httpClient;

    public CampaignService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Creates and starts a new message campaign.
     *
     * @param request Campaign data and contact list.
     * @return Summary of the created campaign.
     */
    public CampaignResponse create(CampaignRequest request) {
        return httpClient.post("/v1/campaigns", request, CampaignResponse.class);
    }

    /**
     * Retrieves campaign details by ID.
     *
     * @param id Campaign identifier.
     * @return Campaign status and metrics.
     */
    public CampaignResponse getById(UUID id) {
        return httpClient.get("/v1/campaigns/" + id.toString(), CampaignResponse.class);
    }
}
