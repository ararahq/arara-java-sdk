package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Internal model to map errors returned by the API.
 */
@Value
@Builder
@Jacksonized
public class AraraError {
    String error;
    String message;
}
