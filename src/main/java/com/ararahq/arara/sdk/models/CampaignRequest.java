package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.time.Instant;
import java.util.List;

/**
 * Payload for creating bulk campaigns.
 */
@Value
@Builder
@Jacksonized
public class CampaignRequest {
    String name;
    String templateName;
    String sender;
    List<CampaignContactRequest> contacts;
    /** Optional schedule; null sends immediately. */
    Instant scheduledAt;
}
