package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Owner of the API key, returned by {@code GET /auth/me}.
 */
@Value
@Builder
@Jacksonized
public class UserResponse {
    String name;
    String email;
    String role;
    boolean emailPending;
}
