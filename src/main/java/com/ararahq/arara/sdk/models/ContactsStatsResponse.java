package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Aggregate contact counts by lifecycle stage.
 */
@Value
@Builder
@Jacksonized
public class ContactsStatsResponse {
    long total;
    long newCount;
    long engaged;
    long silent;
    long dormant;
    long optedOut;
}
