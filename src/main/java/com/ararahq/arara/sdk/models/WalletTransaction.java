package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single wallet ledger entry.
 */
@Value
@Builder
@Jacksonized
public class WalletTransaction {
    String id;
    BigDecimal amount;
    String type;
    String description;
    String referenceId;
    String mode;
    Instant createdAt;
}
