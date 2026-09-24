package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Page of campaigns: {@code {"content": [...], "totalPages", "totalElements"}}.
 */
@Value
@Builder
@Jacksonized
public class CampaignPage {
    List<CampaignListItem> content;
    int totalPages;
    long totalElements;
}
