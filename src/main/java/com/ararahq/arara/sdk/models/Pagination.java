package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Pagination metadata of a {@link PaginatedResponse}.
 */
@Value
@Builder
@Jacksonized
public class Pagination {
    int page;
    int size;
    long totalElements;
    int totalPages;
}
