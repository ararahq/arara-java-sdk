package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;

/**
 * Represents the status and metadata of a sent message.
 */
@Value
@Builder
@Jacksonized
public class MessageResponse {
    String id;
    String status;
    String mode;
    String sender;
    String receiver;
    String body;
    BigDecimal cost;
}
