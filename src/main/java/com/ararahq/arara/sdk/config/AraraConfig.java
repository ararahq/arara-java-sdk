package com.ararahq.arara.sdk.config;

import lombok.Builder;
import lombok.Value;
import java.time.Duration;

/**
 * Global configuration for the Arara SDK.
 */
@Value
@Builder
public class AraraConfig {
    @Builder.Default
    String baseUrl = "https://api.ararahq.com/api";

    String apiKey;

    @Builder.Default
    Duration connectTimeout = Duration.ofSeconds(10);

    @Builder.Default
    Duration readTimeout = Duration.ofSeconds(30);

    @Builder.Default
    int maxRetries = 3;
}
