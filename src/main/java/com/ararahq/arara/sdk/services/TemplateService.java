package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CreateTemplateRequest;
import com.ararahq.arara.sdk.models.TemplateResponse;
import com.ararahq.arara.sdk.models.TemplateStatusResponse;
import com.ararahq.arara.sdk.utils.ValidationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing WhatsApp templates.
 */
public class TemplateService {
    private final AraraHttpClient httpClient;

    public TemplateService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Creates and submits a new template to Meta.
     *
     * @param request The template definition.
     * @return The created template details.
     */
    public TemplateResponse create(CreateTemplateRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.post("/v1/templates", request, TemplateResponse.class);
    }

    /**
     * Lists all templates for the authenticated account.
     *
     * @return A list of templates.
     */
    public List<TemplateResponse> list() {
        TemplateResponse[] templates = httpClient.get("/v1/templates", TemplateResponse[].class);
        return templates != null ? Arrays.asList(templates) : List.of();
    }

    /**
     * Retrieves a specific template by ID.
     *
     * @param id The template UUID.
     * @return Template details.
     */
    public TemplateResponse getById(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get("/v1/templates/" + id, TemplateResponse.class);
    }

    /**
     * Deletes a template.
     *
     * @param id The template UUID.
     */
    public void delete(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        httpClient.delete("/v1/templates/" + id);
    }

    /**
     * Checks and refreshes the approval status of a template.
     *
     * @param id The template UUID.
     * @return Current approval status from provider.
     */
    public TemplateStatusResponse getStatus(UUID id) {
        ValidationUtils.checkNotNull(id, "id");
        return httpClient.get("/v1/templates/" + id + "/status", TemplateStatusResponse.class);
    }
}
