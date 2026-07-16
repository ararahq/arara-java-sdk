package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.UUID;

/**
 * A single message exchanged with a contact.
 */
@Value
@Builder
@Jacksonized
public class ContactMessageItem {
    UUID id;
    String direction;
    String status;
    String templateName;
    String body;
    String createdAt;
}
