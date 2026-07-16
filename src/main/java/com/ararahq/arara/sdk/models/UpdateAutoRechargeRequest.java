package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;

/**
 * Payload for updating auto-recharge settings.
 */
@Value
@Builder
@Jacksonized
public class UpdateAutoRechargeRequest {
    Boolean enabled;
    BigDecimal threshold;
    BigDecimal amount;
}
