package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The organization's WhatsApp business profile and Meta sync state.
 */
@Value
@Builder
@Jacksonized
public class BusinessProfileResponse {
    UUID id;
    String displayName;
    String vertical;
    String description;
    String aboutShort;
    String email;
    List<String> websites;
    Map<String, Object> businessHours;
    String profilePhotoUrl;
    String awayMessage;
    String metaSyncStatus;
    Instant metaSyncedAt;
    String metaSyncError;
    Instant updatedAt;
}
