package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.BusinessProfilePatch;
import com.ararahq.arara.sdk.models.BusinessProfileResponse;
import com.ararahq.arara.sdk.models.OrganizationPlanResponse;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

/**
 * Service for the authenticated organization's plan and business profile.
 */
public class OrganizationService {
    private final AraraHttpClient httpClient;

    public OrganizationService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Returns the organization's business profile. GET /v1/organizations/me/business-profile
     */
    public BusinessProfileResponse me() {
        return httpClient.get("/v1/organizations/me/business-profile", BusinessProfileResponse.class);
    }

    /**
     * Patches the organization's business profile. PATCH /v1/organizations/me/business-profile
     */
    public BusinessProfileResponse updateBusinessProfile(BusinessProfilePatch request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.patch(
                "/v1/organizations/me/business-profile", request, BusinessProfileResponse.class);
    }

    /**
     * Returns the organization's current plan. GET /v1/organizations/me/plan
     */
    public OrganizationPlanResponse getPlan() {
        return httpClient.get("/v1/organizations/me/plan", OrganizationPlanResponse.class);
    }

    /**
     * Downgrades the organization to a plan. PATCH /v1/organizations/me/plan
     */
    public Map<String, Object> changePlan(String plan) {
        return httpClient.patch(
                "/v1/organizations/me/plan", Map.of("plan", plan), new TypeReference<Map<String, Object>>() {
                });
    }
}
