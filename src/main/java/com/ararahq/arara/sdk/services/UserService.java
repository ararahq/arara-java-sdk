package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.UpdateUserRequest;
import com.ararahq.arara.sdk.models.UserResponse;

/**
 * Service for managing users and profile information.
 */
public class UserService {
    private final AraraHttpClient httpClient;

    public UserService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Returns the currently authenticated user's data.
     *
     * @return Logged-in user's profile.
     */
    public UserResponse me() {
        return httpClient.get("/users/me", UserResponse.class);
    }

    /**
     * Updates the currently authenticated user's profile.
     *
     * @param request The update payload (name, phoneNumber).
     * @return The updated user profile.
     */
    public UserResponse updateMe(UpdateUserRequest request) {
        return httpClient.patch("/users/me", request, UserResponse.class);
    }
}
