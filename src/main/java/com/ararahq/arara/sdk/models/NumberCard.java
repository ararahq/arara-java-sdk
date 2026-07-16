package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.time.Instant;

/**
 * A dedicated or shared phone number with health metadata.
 */
@Value
@Builder
@Jacksonized
public class NumberCard {
    String id;
    String name;
    String alias;
    String description;
    String phoneNumber;
    String type;
    boolean isDefault;
    String status;
    String qualityScore;
    String messagingTier;
    Instant verifiedAt;
    Instant lastHealthCheckAt;
    String provider;
    Instant createdAt;
    long messagesLast7d;
    long messagesLast30d;
}
