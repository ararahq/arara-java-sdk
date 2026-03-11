package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Campaign response with status and metrics.
 */
@Value
@Builder
@Jacksonized
public class CampaignResponse {
    UUID id;
    String name;
    String status;
    int totalMessages;
    BigDecimal totalCost;
}
