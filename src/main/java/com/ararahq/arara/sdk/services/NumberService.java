package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.NumbersResponse;
import com.ararahq.arara.sdk.models.RequestNumberRequest;
import com.ararahq.arara.sdk.models.UpdateNumberRequest;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Service for managing the organization's dedicated numbers.
 */
public class NumberService {
    private static final String BASE = "/v1/organizations/me/numbers";
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {
            };
    private static final TypeReference<List<Map<String, Object>>> MAP_LIST_TYPE =
            new TypeReference<List<Map<String, Object>>>() {
            };

    private final AraraHttpClient httpClient;

    public NumberService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Lists numbers and plan slot info. GET /v1/organizations/me/numbers
     */
    public NumbersResponse list() {
        return httpClient.get(BASE, NumbersResponse.class);
    }

    /**
     * Updates a number. PATCH /v1/organizations/me/numbers/{id}
     */
    public Map<String, Object> update(String id, UpdateNumberRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.patch(BASE + "/" + id, request, MAP_TYPE);
    }

    /**
     * Soft-deletes a number. DELETE /v1/organizations/me/numbers/{id}
     */
    public void delete(String id) {
        httpClient.delete(BASE + "/" + id);
    }

    /**
     * Requests a new dedicated number. POST /v1/organizations/me/numbers/request
     */
    public Map<String, Object> request(RequestNumberRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.post(BASE + "/request", request, MAP_TYPE);
    }

    /**
     * Lists pending and resolved number requests. GET /v1/organizations/me/numbers/requests
     */
    public List<Map<String, Object>> listRequests() {
        return httpClient.get(BASE + "/requests", MAP_LIST_TYPE);
    }

    /**
     * Triggers a provider sync. POST /v1/organizations/me/numbers/{id}/sync
     */
    public Map<String, Object> sync(String id) {
        return httpClient.post(BASE + "/" + id + "/sync", null, MAP_TYPE);
    }

    /**
     * Returns warming recommendations. GET /v1/organizations/me/numbers/{id}/warming
     */
    public Map<String, Object> warming(String id) {
        return httpClient.get(BASE + "/" + id + "/warming", MAP_TYPE);
    }
}
