package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Payload for creating a WhatsApp smart link.
 */
@Value
@Builder
@Jacksonized
public class CreateWhatsAppSmartLinkRequest {
    String name;
    String phoneNumber;
    String defaultText;
    String qrCodeColor;
}
