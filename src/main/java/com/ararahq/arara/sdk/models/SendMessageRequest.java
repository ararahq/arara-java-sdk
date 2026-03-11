package com.ararahq.arara.sdk.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.time.Instant;
import java.util.List;

/**
 * Payload for sending messages via WhatsApp.
 * Supports both templates and free text (following API rules).
 */
@Value
@Builder
@Jacksonized
public class SendMessageRequest {
    /** The recipient's number (e.g., "whatsapp:+5511999998888") */
    String receiver;

    /** Optional sender number */
    String sender;

    /** Name of the pre-approved template in Meta */
    String templateName;

    /**
     * Template variables. Accepts "variables" or "templateVariables" in
     * deserialization
     */
    @JsonAlias({ "variables", "templateVariables" })
    List<String> templateVariables;

    /** Free text (used if templateName is null) */
    String body;

    /** Date/time for scheduling */
    @JsonProperty("scheduled_at")
    Instant scheduledAt;

    /** Sending mode (e.g., "LIVE", "TEST") */
    String mode;

    /** Optional parameter for Smart Links */
    String smartLinkParam;

    /** Optional URL for Smart Links */
    String smartLinkUrl;

    /** Optional media URL (header) */
    @JsonProperty("media_url")
    String mediaUrl;
}
