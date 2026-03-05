package com.ararahq.arara.sdk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
