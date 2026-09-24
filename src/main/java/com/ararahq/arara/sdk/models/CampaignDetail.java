package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Full campaign detail returned by {@code GET /v1/campaigns/{id}}.
 */
@Value
@Builder
@Jacksonized
public class CampaignDetail {
    UUID id;
    String name;
    String status;
    String templateName;
    String templateBody;
    int totalMessages;
    int sentCount;
    int deliveredCount;
    int readCount;
    int failedCount;
    int clickedCount;
    int convertedCount;
    BigDecimal convertedValue;
    int replyCount;
    int holdoutCount;
    int blockedCount;
    List<BlockReason> blockReasons;
    int refundCount;
    BigDecimal refundValue;
    BigDecimal totalCost;
    Instant scheduledAt;
    Instant startedAt;
    Instant finishedAt;
    Instant createdAt;

    /**
     * Why contacts were blocked, in customer language ({@code motivo}) with a count ({@code quantidade}).
     */
    @Value
    @Builder
    @Jacksonized
    public static class BlockReason {
        String motivo;
        long quantidade;
    }
}
