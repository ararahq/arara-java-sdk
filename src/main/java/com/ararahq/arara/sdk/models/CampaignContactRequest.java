package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * Contact information for a campaign message.
 */
@Value
@Builder
@Jacksonized
public class CampaignContactRequest {
    String to;
    List<String> variables;
}
