package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.GeneratedApiKey;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiKeyService Tests")
class ApiKeyServiceTest {

    private static final String BASE = "/v1/api-keys";

    @Mock
    private AraraHttpClient httpClient;

    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp() {
        apiKeyService = new ApiKeyService(httpClient);
    }

    @Test
    @DisplayName("should list API keys")
    void shouldListApiKeys() {
        List<Map<String, Object>> expected = Arrays.asList(Map.of("id", "key_1"));
        when(httpClient.get(eq(BASE), any(TypeReference.class))).thenReturn(expected);

        List<Map<String, Object>> result = apiKeyService.list();

        assertEquals(1, result.size());
        verify(httpClient, times(1)).get(eq(BASE), any(TypeReference.class));
    }

    @Test
    @DisplayName("should create an API key with an explicit mode")
    void shouldCreateWithMode() {
        GeneratedApiKey expected = GeneratedApiKey.builder().plainTextKey("ara_live_abc").build();
        when(httpClient.post(eq(BASE + "?mode=TEST"), isNull(), eq(GeneratedApiKey.class)))
                .thenReturn(expected);

        GeneratedApiKey result = apiKeyService.create("TEST");

        assertEquals("ara_live_abc", result.getPlainTextKey());
        verify(httpClient, times(1)).post(eq(BASE + "?mode=TEST"), isNull(), eq(GeneratedApiKey.class));
    }

    @Test
    @DisplayName("should create a LIVE API key by default")
    void shouldCreateLiveByDefault() {
        GeneratedApiKey expected = GeneratedApiKey.builder().plainTextKey("ara_live_xyz").build();
        when(httpClient.post(eq(BASE + "?mode=LIVE"), isNull(), eq(GeneratedApiKey.class)))
                .thenReturn(expected);

        GeneratedApiKey result = apiKeyService.create();

        assertEquals("ara_live_xyz", result.getPlainTextKey());
        verify(httpClient, times(1)).post(eq(BASE + "?mode=LIVE"), isNull(), eq(GeneratedApiKey.class));
    }
}
