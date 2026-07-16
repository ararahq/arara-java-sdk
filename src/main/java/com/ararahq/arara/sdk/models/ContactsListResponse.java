package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * Paginated list of contacts.
 */
@Value
@Builder
@Jacksonized
public class ContactsListResponse {
    List<ContactResponse> contacts;
    long total;
    int page;
    int size;
    int totalPages;
}
