package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Basic user information returned by the API.
 */
@Value
@Builder
@Jacksonized
public class UserResponse {
    String name;
    String email;
    String role;
    String phoneNumber;
    String documentNumber;
    boolean needsInitialOnboarding;
}
