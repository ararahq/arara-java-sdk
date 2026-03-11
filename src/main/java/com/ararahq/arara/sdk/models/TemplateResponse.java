package com.ararahq.arara.sdk.models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Complete details of a WhatsApp template.
 */
@Value
@Builder
@Jacksonized
public class TemplateResponse {
    UUID id;
    String name;
    String formattedName;
    String category;
    String originalCategory;
    String language;
    String providerName;
    String providerTemplateId;
    String providerStatus;
    String rejectionReason;

    String bodyPreview;
    JsonNode structureJson;

    Map<String, Object> usageGuide;
    Map<String, String> variablesSchema;

    Instant createdAt;
    Instant updatedAt;
}
