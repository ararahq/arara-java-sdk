package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;
import java.util.Map;

/**
 * Payload for patching the organization's WhatsApp business profile.
 */
@Value
@Builder
@Jacksonized
public class BusinessProfilePatch {
    String displayName;
    String vertical;
    String description;
    String aboutShort;
    String email;
    List<String> websites;
    Map<String, Object> businessHours;
    String profilePhotoUrl;
    String awayMessage;
}
