package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a single contact and its engagement metadata.
 */
@Value
@Builder
@Jacksonized
public class ContactResponse {
    UUID id;
    String name;
    String phone;
    String email;
    Map<String, Object> attributes;
    List<String> tags;
    String createdAt;
    String lifecycle;
    String source;
    long outboundCount;
    long inboundCount;
    String firstSeenAt;
    String lastOutboundAt;
    String lastInboundAt;
    String lastMessageAt;
    String optOutAt;
    String lastTemplateName;
}
