package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.time.Instant;
import java.util.UUID;

/**
 * A WhatsApp smart link with its short URL and click count.
 */
@Value
@Builder
@Jacksonized
public class WhatsAppSmartLinkResponse {
    UUID id;
    String name;
    String phoneNumber;
    String defaultText;
    String qrCodeColor;
    String code;
    String shortUrl;
    Instant createdAt;
    long clicks;
}
