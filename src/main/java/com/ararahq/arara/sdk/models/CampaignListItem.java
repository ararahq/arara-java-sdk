package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Campaign summary returned by the campaign list.
 */
@Value
@Builder
@Jacksonized
public class CampaignListItem {
    UUID id;
    String name;
    String status;
    String templateName;
    int totalMessages;
    int sentCount;
    int deliveredCount;
    int readCount;
    int failedCount;
    BigDecimal totalCost;
    Instant scheduledAt;
    Instant createdAt;
}
