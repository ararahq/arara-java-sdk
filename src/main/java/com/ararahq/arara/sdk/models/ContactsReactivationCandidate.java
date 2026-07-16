package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * A contact eligible for a reactivation campaign.
 */
@Value
@Builder
@Jacksonized
public class ContactsReactivationCandidate {
    String phone;
    String name;
    String lastMessageAt;
    String lastTemplateName;
}
