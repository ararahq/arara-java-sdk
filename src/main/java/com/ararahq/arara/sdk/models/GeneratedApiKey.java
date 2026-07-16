package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * A freshly generated API key, shown once in plaintext.
 */
@Value
@Builder
@Jacksonized
public class GeneratedApiKey {
    String plainTextKey;
}
