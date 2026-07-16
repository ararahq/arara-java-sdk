package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.ConversationReplyRequest;
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
@DisplayName("ConversationService Tests")
class ConversationServiceTest {

    @Mock
    private AraraHttpClient httpClient;

    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        conversationService = new ConversationService(httpClient);
    }

    @Test
    @DisplayName("should list conversations with base pagination when filters are null")
    void shouldListWithoutFilters() {
        Map<String, Object> expected = Map.of("data", Arrays.asList());
        when(httpClient.get(eq("/v1/conversations?page=0&size=20"), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = conversationService.list(null, null, 0, 20);

        assertNotNull(result);
        verify(httpClient, times(1)).get(eq("/v1/conversations?page=0&size=20"), any(TypeReference.class));
    }

    @Test
    @DisplayName("should append status and leadStatus filters to the path")
    void shouldListWithFilters() {
        String path = "/v1/conversations?page=1&size=15&status=OPEN&leadStatus=HOT";
        Map<String, Object> expected = Map.of("total", 4);
        when(httpClient.get(eq(path), any(TypeReference.class))).thenReturn(expected);

        Map<String, Object> result = conversationService.list("OPEN", "HOT", 1, 15);

        assertEquals(4, result.get("total"));
        verify(httpClient, times(1)).get(eq(path), any(TypeReference.class));
    }

    @Test
    @DisplayName("should return lead counts by status")
    void shouldReturnLeadStats() {
        Map<String, Object> expected = Map.of("HOT", 2);
        when(httpClient.get(eq("/v1/conversations/lead-stats"), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = conversationService.leadStats();

        assertEquals(2, result.get("HOT"));
        verify(httpClient, times(1)).get(eq("/v1/conversations/lead-stats"), any(TypeReference.class));
    }

    @Test
    @DisplayName("should list messages of a conversation")
    void shouldListMessages() {
        String path = "/v1/conversations/conv_1/messages?page=0&size=30";
        Map<String, Object> expected = Map.of("total", 12);
        when(httpClient.get(eq(path), any(TypeReference.class))).thenReturn(expected);

        Map<String, Object> result = conversationService.messages("conv_1", 0, 30);

        assertEquals(12, result.get("total"));
        verify(httpClient, times(1)).get(eq(path), any(TypeReference.class));
    }

    @Test
    @DisplayName("should send a free-text reply")
    void shouldReply() {
        ConversationReplyRequest request = ConversationReplyRequest.builder()
                .conversationId(UUID.randomUUID())
                .body("Hi there")
                .build();
        Map<String, Object> expected = Map.of("status", "SENT");
        when(httpClient.post(eq("/v1/conversations/reply"), eq(request), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = conversationService.reply(request);

        assertEquals("SENT", result.get("status"));
        verify(httpClient, times(1))
                .post(eq("/v1/conversations/reply"), eq(request), any(TypeReference.class));
    }

    @Test
    @DisplayName("should reject null reply request")
    void shouldRejectNullReply() {
        assertThrows(AraraException.class, () -> conversationService.reply(null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should update a conversation status")
    void shouldUpdateStatus() {
        Map<String, Object> expected = Map.of("status", "CLOSED");
        when(httpClient.patch(
                eq("/v1/conversations/conv_9/status"), eq(Map.of("status", "CLOSED")), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = conversationService.updateStatus("conv_9", "CLOSED");

        assertEquals("CLOSED", result.get("status"));
        verify(httpClient, times(1)).patch(
                eq("/v1/conversations/conv_9/status"), eq(Map.of("status", "CLOSED")), any(TypeReference.class));
    }

    @Test
    @DisplayName("should check 24h window status for phones")
    void shouldCheckWindowStatus() {
        List<String> phones = Arrays.asList("+5511999998888", "+5511988887777");
        Map<String, Object> expected = Map.of("open", 1);
        when(httpClient.post(
                eq("/v1/conversations/window-status"), eq(Map.of("phones", phones)), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = conversationService.windowStatus(phones);

        assertEquals(1, result.get("open"));
        verify(httpClient, times(1)).post(
                eq("/v1/conversations/window-status"), eq(Map.of("phones", phones)), any(TypeReference.class));
    }

    @Test
    @DisplayName("should reject null phones list for window status")
    void shouldRejectNullPhones() {
        assertThrows(AraraException.class, () -> conversationService.windowStatus(null));
        verifyNoInteractions(httpClient);
    }
}
