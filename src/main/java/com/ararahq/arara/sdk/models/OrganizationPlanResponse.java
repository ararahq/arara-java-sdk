package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.time.Instant;
import java.util.List;

/**
 * Current plan, per-category pricing and enabled features for the organization.
 */
@Value
@Builder
@Jacksonized
public class OrganizationPlanResponse {
    String current;
    int monthlyPriceCents;
    int marketingPriceCents;
    int utilityPriceCents;
    int authPriceCents;
    List<String> features;
    Instant nextBillingAt;
}
