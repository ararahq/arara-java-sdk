package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * List of contacts eligible for reactivation.
 */
@Value
@Builder
@Jacksonized
public class ContactsReactivationResponse {
    long total;
    List<ContactsReactivationCandidate> candidates;
}
