package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * Recent message history for a contact.
 */
@Value
@Builder
@Jacksonized
public class ContactMessagesResponse {
    String phone;
    int total;
    List<ContactMessageItem> messages;
}
