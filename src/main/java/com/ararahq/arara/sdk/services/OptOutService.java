package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.utils.QueryString;
import com.ararahq.arara.sdk.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Channel opt-outs (numbers that must not receive messages). Requires an ADMIN key.
 */
public class OptOutService {
    private static final String BASE = "/v1/opt-outs";
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {
            };

    private final AraraHttpClient httpClient;

    public OptOutService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Lists opt-outs. GET /v1/opt-outs
     */
    public Map<String, Object> list() {
        return httpClient.get(BASE, MAP_TYPE);
    }

    /**
     * Registers an opt-out. POST /v1/opt-outs
     *
     * @param phone  Phone number to block.
     * @param reason Optional reason.
     */
    public Map<String, Object> add(String phone, String reason) {
        ValidationUtils.checkNotNull(phone, "phone");
        Map<String, Object> body = new HashMap<>();
        body.put("phone", phone);
        body.put("reason", reason);
        return httpClient.post(BASE, body, MAP_TYPE);
    }

    /**
     * Returns the opt-out state of a number. GET /v1/opt-outs/{phone}
     */
    public Map<String, Object> get(String phone) {
        ValidationUtils.checkNotNull(phone, "phone");
        return httpClient.get(BASE + "/" + QueryString.encodePathSegment(phone), MAP_TYPE);
    }

    /**
     * Removes an opt-out. DELETE /v1/opt-outs/{phone}
     */
    public Map<String, Object> remove(String phone) {
        ValidationUtils.checkNotNull(phone, "phone");
        return httpClient.delete(BASE + "/" + QueryString.encodePathSegment(phone), MAP_TYPE);
    }
}
