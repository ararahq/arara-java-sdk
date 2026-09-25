package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Payload for sending one template to up to 1000 recipients in a single call.
 */
@Value
@Builder
@Jacksonized
public class BatchMessageRequest {
    String templateName;
    List<BatchMessageItem> messages;

    /**
     * One recipient of a batch send.
     */
    @Value
    @Builder
    @Jacksonized
    public static class BatchMessageItem {
        String receiver;
        List<String> templateVariables;
        String smartLinkParam;
        String smartLinkUrl;
    }
}
