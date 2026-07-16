package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.ContactMessagesResponse;
import com.ararahq.arara.sdk.models.ContactPatchRequest;
import com.ararahq.arara.sdk.models.ContactRequest;
import com.ararahq.arara.sdk.models.ContactResponse;
import com.ararahq.arara.sdk.models.ContactsBatchResponse;
import com.ararahq.arara.sdk.models.ContactsListResponse;
import com.ararahq.arara.sdk.models.ContactsReactivationResponse;
import com.ararahq.arara.sdk.models.ContactsStatsResponse;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Service for managing contacts and their engagement history.
 */
public class ContactService {
    private final AraraHttpClient httpClient;

    public ContactService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Lists contacts. GET /v1/contacts
     */
    public ContactsListResponse list(int page, int size, String query, String lifecycle) {
        StringBuilder path = new StringBuilder("/v1/contacts?page=").append(page).append("&size=").append(size);
        if (query != null) {
            path.append("&q=").append(query);
        }
        if (lifecycle != null) {
            path.append("&lifecycle=").append(lifecycle);
        }
        return httpClient.get(path.toString(), ContactsListResponse.class);
    }

    /**
     * Imports a batch of contacts. POST /v1/contacts/batch
     */
    public ContactsBatchResponse importBatch(List<ContactRequest> contacts) {
        ValidationUtils.checkNotNull(contacts, "contacts");
        return httpClient.post("/v1/contacts/batch", contacts, ContactsBatchResponse.class);
    }

    /**
     * Returns aggregate contact stats. GET /v1/contacts/stats
     */
    public ContactsStatsResponse stats() {
        return httpClient.get("/v1/contacts/stats", ContactsStatsResponse.class);
    }

    /**
     * Lists contacts eligible for reactivation. GET /v1/contacts/reactivation
     */
    public ContactsReactivationResponse reactivationCandidates(int limit) {
        return httpClient.get("/v1/contacts/reactivation?limit=" + limit, ContactsReactivationResponse.class);
    }

    /**
     * Lists distinct contact tags. GET /v1/contacts/tags
     */
    public Map<String, List<String>> listTags() {
        return httpClient.get("/v1/contacts/tags", new TypeReference<Map<String, List<String>>>() {
        });
    }

    /**
     * Retrieves a contact by phone. GET /v1/contacts/{phone}
     */
    public ContactResponse get(String phone) {
        return httpClient.get("/v1/contacts/" + phone, ContactResponse.class);
    }

    /**
     * Patches a contact by phone. PATCH /v1/contacts/{phone}
     */
    public ContactResponse update(String phone, ContactPatchRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.patch("/v1/contacts/" + phone, request, ContactResponse.class);
    }

    /**
     * Lists recent messages for a contact. GET /v1/contacts/{phone}/messages
     */
    public ContactMessagesResponse messages(String phone, int limit) {
        return httpClient.get("/v1/contacts/" + phone + "/messages?limit=" + limit, ContactMessagesResponse.class);
    }
}
