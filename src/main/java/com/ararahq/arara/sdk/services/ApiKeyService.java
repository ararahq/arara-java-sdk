package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.GeneratedApiKey;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Service for managing API keys.
 */
public class ApiKeyService {
    private static final String BASE = "/v1/api-keys";

    private final AraraHttpClient httpClient;

    public ApiKeyService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Lists API keys. GET /v1/api-keys
     */
    public List<Map<String, Object>> list() {
        return httpClient.get(BASE, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    /**
     * Creates an API key. POST /v1/api-keys?mode=LIVE
     */
    public GeneratedApiKey create(String mode) {
        return httpClient.post(BASE + "?mode=" + mode, null, GeneratedApiKey.class);
    }

    /**
     * Creates a LIVE API key. POST /v1/api-keys?mode=LIVE
     */
    public GeneratedApiKey create() {
        return create("LIVE");
    }
}
