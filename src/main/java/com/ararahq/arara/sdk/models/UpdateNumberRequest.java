package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Payload for updating a number's alias, name, description or default flag.
 */
@Value
@Builder
@Jacksonized
public class UpdateNumberRequest {
    String alias;
    Boolean isDefault;
    String name;
    String description;
}
