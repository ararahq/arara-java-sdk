package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Plan slot usage for dedicated numbers.
 */
@Value
@Builder
@Jacksonized
public class NumbersSlot {
    int used;
    int max;
    String planLabel;
    boolean atCap;
    boolean noEntitlement;
    int monthlyPriceCents;
    int monthlyTotalCents;
}
