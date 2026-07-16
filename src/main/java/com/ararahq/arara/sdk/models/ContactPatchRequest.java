package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * Payload for patching an existing contact.
 */
@Value
@Builder
@Jacksonized
public class ContactPatchRequest {
    String name;
    String email;
    List<String> tags;
}
