package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Payload for updating a WhatsApp smart link.
 */
@Value
@Builder
@Jacksonized
public class UpdateWhatsAppSmartLinkRequest {
    String name;
    String defaultText;
    String qrCodeColor;
}
