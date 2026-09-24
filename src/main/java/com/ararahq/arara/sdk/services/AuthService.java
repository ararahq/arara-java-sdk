package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.UserResponse;

/**
 * Identity of the API key owner. Requires an ADMIN key.
 */
public class AuthService {
    private final AraraHttpClient httpClient;

    public AuthService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Returns the user that owns the API key. GET /auth/me
     */
    public UserResponse me() {
        return httpClient.get("/auth/me", UserResponse.class);
    }
}
