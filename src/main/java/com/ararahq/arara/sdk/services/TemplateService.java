package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CreateTemplateRequest;
import com.ararahq.arara.sdk.models.PaginatedResponse;
import com.ararahq.arara.sdk.models.TemplateResponse;
import com.ararahq.arara.sdk.models.TemplateStatusResponse;
import com.ararahq.arara.sdk.utils.QueryString;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.UUID;

/**
 * Service for managing WhatsApp templates. Single-template operations take the template id (UUID),
 * never the name: to find by name, use {@link #list(String, String, Integer, Integer)} with the name filter.
 */
public class TemplateService {
    private static final String BASE = "/v1/templates";
    private static final TypeReference<PaginatedResponse<TemplateResponse>> PAGE_TYPE =
            new TypeReference<PaginatedResponse<TemplateResponse>>() {
            };
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {
            };

    private final AraraHttpClient httpClient;

    public TemplateService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Creates and submits a new template to Meta. POST /v1/templates
     */
    public TemplateResponse create(CreateTemplateRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.post(BASE, request, TemplateResponse.class);
    }

    /**
     * Lists the first page of templates with the API defaults. GET /v1/templates
     */
    public PaginatedResponse<TemplateResponse> list() {
        return list(null, null, null, null);
    }

    /**
     * Lists templates. GET /v1/templates
     *
     * @param name   Optional name filter.
     * @param status Optional status filter (e.g. APPROVED).
     * @param page   Optional zero-based page.
     * @param size   Optional page size.
     */
    public PaginatedResponse<TemplateResponse> list(String name, String status, Integer page, Integer size) {
        String path = QueryString.create()
                .add("name", name)
                .add("status", status)
                .add("page", page)
                .add("size", size)
                .appendTo(BASE);
        return httpClient.get(path, PAGE_TYPE);
    }

    /**
     * Retrieves a template by id. GET /v1/templates/{id}
     */
    public TemplateResponse getById(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get(BASE + "/" + id, TemplateResponse.class);
    }

    /**
     * Deletes a template by id. DELETE /v1/templates/{id}
     */
    public void delete(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        httpClient.delete(BASE + "/" + id);
    }

    /**
     * Refreshes and returns the approval status of a template. GET /v1/templates/{id}/status
     */
    public TemplateStatusResponse getStatus(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get(BASE + "/" + id + "/status", TemplateStatusResponse.class);
    }

    /**
     * Aggregated analytics of all templates. GET /v1/templates/analytics
     *
     * @param period Optional window (e.g. 7d, 30d); API default is 30d.
     */
    public Map<String, Object> analytics(String period) {
        return httpClient.get(QueryString.create().add("period", period).appendTo(BASE + "/analytics"), MAP_TYPE);
    }

    /**
     * Analytics of one template. GET /v1/templates/{id}/analytics
     */
    public Map<String, Object> analytics(UUID id, String period) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get(
                QueryString.create().add("period", period).appendTo(BASE + "/" + id + "/analytics"), MAP_TYPE);
    }
}
