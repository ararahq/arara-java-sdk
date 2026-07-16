package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Payload for requesting a new dedicated number.
 */
@Value
@Builder
@Jacksonized
public class RequestNumberRequest {
    String reason;
    String expectedVolume;
    String areaCode;
    String displayName;
    String profilePictureUrl;
}
