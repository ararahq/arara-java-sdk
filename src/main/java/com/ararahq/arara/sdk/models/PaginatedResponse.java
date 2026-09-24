package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Page envelope used by templates, smart links, flows and charges:
 * {@code {"data": [...], "pagination": {page, size, totalElements, totalPages}}}.
 *
 * @param <T> Item type.
 */
@Value
@Builder
@Jacksonized
public class PaginatedResponse<T> {
    List<T> data;
    Pagination pagination;
}
