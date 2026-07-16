package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.UUID;

/**
 * Payload for replying with free text inside an open conversation window.
 */
@Value
@Builder
@Jacksonized
public class ConversationReplyRequest {
    UUID conversationId;
    String body;
}
