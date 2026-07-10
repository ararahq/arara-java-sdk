package com.ararahq.arara.sdk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Arara Entry Point Tests")
class AraraTest {

    @Test
    @DisplayName("should initialize services correctly")
    void shouldInitializeServices() {
        Arara arara = Arara.builder()
                .apiKey("test-key")
                .build();

        assertNotNull(arara.getMessages());
        assertNotNull(arara.getUsers());
        assertNotNull(arara.getCampaigns());
        assertNotNull(arara.getTemplates());
    }

    @Test
    @DisplayName("should throw exception without API key")
    void shouldThrowExceptionWithoutApiKey() {
        assertThrows(RuntimeException.class, () -> Arara.builder().build());
    }

    @Test
    @DisplayName("should build with timeouts and retries configured")
    void shouldBuildWithTimeoutsAndRetries() {
        Arara arara = Arara.builder()
                .apiKey("test-key")
                .baseUrl("https://api.example.com")
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15))
                .writeTimeout(Duration.ofSeconds(15))
                .callTimeout(Duration.ofSeconds(60))
                .maxRetries(1)
                .build();

        assertNotNull(arara.getMessages());
        assertNotNull(arara.getCampaigns());
    }
}
