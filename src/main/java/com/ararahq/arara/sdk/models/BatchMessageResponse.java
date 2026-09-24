package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * Result of a batch send.
 */
@Value
@Builder
@Jacksonized
public class BatchMessageResponse {
    String batchId;
    String templateName;
    int total;
    int accepted;
    BigDecimal totalCost;
    List<Item> messages;

    /**
     * Per-recipient outcome of a batch send.
     */
    @Value
    @Builder
    @Jacksonized
    public static class Item {
        String id;
        String receiver;
        String status;
        BigDecimal cost;
    }
}
