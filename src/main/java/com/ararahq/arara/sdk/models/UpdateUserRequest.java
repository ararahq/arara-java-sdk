package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Payload for updating user profile information.
 */
@Value
@Builder
@Jacksonized
public class UpdateUserRequest {
    String name;
    String phoneNumber;
}
