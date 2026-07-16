package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Current auto-recharge configuration for the wallet.
 */
@Value
@Builder
@Jacksonized
public class AutoRechargeSettings {
    boolean enabled;
    BigDecimal threshold;
    BigDecimal amount;
    Instant lastAttemptAt;
    String lastFailureReason;
}
