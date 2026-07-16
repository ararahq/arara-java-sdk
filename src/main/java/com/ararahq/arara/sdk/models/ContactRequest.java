package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.Map;

/**
 * Payload for creating or updating a contact in a batch import.
 */
@Value
@Builder
@Jacksonized
public class ContactRequest {
    String name;
    String phone;
    String email;
    Map<String, Object> attributes;
}
