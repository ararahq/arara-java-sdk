package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CreateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.UpdateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.WhatsAppSmartLinkResponse;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmartLinkService Tests")
class SmartLinkServiceTest {

    private static final String BASE = "/v1/smart-links/whatsapp";

    @Mock
    private AraraHttpClient httpClient;

    private SmartLinkService smartLinkService;

    @BeforeEach
    void setUp() {
        smartLinkService = new SmartLinkService(httpClient);
    }

    @Test
    @DisplayName("should create a smart link")
    void shouldCreateSmartLink() {
        CreateWhatsAppSmartLinkRequest request = CreateWhatsAppSmartLinkRequest.builder()
                .name("Promo")
                .phoneNumber("+551140001000")
                .defaultText("Hi")
                .build();
        WhatsAppSmartLinkResponse expected = WhatsAppSmartLinkResponse.builder()
                .id(UUID.randomUUID())
                .name("Promo")
                .code("abc123")
                .build();
        when(httpClient.post(BASE, request, WhatsAppSmartLinkResponse.class)).thenReturn(expected);

        WhatsAppSmartLinkResponse result = smartLinkService.create(request);

        assertEquals("Promo", result.getName());
        verify(httpClient, times(1)).post(BASE, request, WhatsAppSmartLinkResponse.class);
    }

    @Test
    @DisplayName("should reject null create request")
    void shouldRejectNullCreate() {
        assertThrows(AraraException.class, () -> smartLinkService.create(null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should update a smart link via PUT")
    void shouldUpdateSmartLink() {
        UpdateWhatsAppSmartLinkRequest request = UpdateWhatsAppSmartLinkRequest.builder()
                .name("Promo v2")
                .defaultText("Hello")
                .build();
        WhatsAppSmartLinkResponse expected = WhatsAppSmartLinkResponse.builder().name("Promo v2").build();
        when(httpClient.put(BASE + "/link_1", request, WhatsAppSmartLinkResponse.class)).thenReturn(expected);

        WhatsAppSmartLinkResponse result = smartLinkService.update("link_1", request);

        assertEquals("Promo v2", result.getName());
        verify(httpClient, times(1)).put(BASE + "/link_1", request, WhatsAppSmartLinkResponse.class);
    }

    @Test
    @DisplayName("should reject null update request")
    void shouldRejectNullUpdate() {
        assertThrows(AraraException.class, () -> smartLinkService.update("link_1", null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should list smart links")
    void shouldListSmartLinks() {
        List<WhatsAppSmartLinkResponse> expected = Arrays.asList(
                WhatsAppSmartLinkResponse.builder().name("Promo").build());
        when(httpClient.get(eq(BASE), any(TypeReference.class))).thenReturn(expected);

        List<WhatsAppSmartLinkResponse> result = smartLinkService.list();

        assertEquals(1, result.size());
        verify(httpClient, times(1)).get(eq(BASE), any(TypeReference.class));
    }

    @Test
    @DisplayName("should return click stats for a smart link")
    void shouldReturnStats() {
        Map<String, Object> expected = Map.of("clicks", 42);
        when(httpClient.get(eq(BASE + "/link_1/stats"), any(TypeReference.class))).thenReturn(expected);

        Map<String, Object> result = smartLinkService.stats("link_1");

        assertNotNull(result);
        assertEquals(42, result.get("clicks"));
        verify(httpClient, times(1)).get(eq(BASE + "/link_1/stats"), any(TypeReference.class));
    }
}
