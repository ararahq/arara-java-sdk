package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Approval status details for a WhatsApp template.
 */
@Value
@Builder
@Jacksonized
public class TemplateStatusResponse {
    String status;
    String rejectionReason;
    String category;
}
