package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.NumberCard;
import com.ararahq.arara.sdk.models.NumbersResponse;
import com.ararahq.arara.sdk.models.NumbersSlot;
import com.ararahq.arara.sdk.models.RequestNumberRequest;
import com.ararahq.arara.sdk.models.UpdateNumberRequest;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NumberService Tests")
class NumberServiceTest {

    private static final String BASE = "/v1/organizations/me/numbers";

    @Mock
    private AraraHttpClient httpClient;

    private NumberService numberService;

    @BeforeEach
    void setUp() {
        numberService = new NumberService(httpClient);
    }

    @Test
    @DisplayName("should list numbers and slot info")
    void shouldListNumbers() {
        NumbersResponse expected = NumbersResponse.builder()
                .numbers(Arrays.asList(NumberCard.builder().id("num_1").phoneNumber("+551140001000").build()))
                .slot(NumbersSlot.builder().used(1).max(3).build())
                .build();
        when(httpClient.get(BASE, NumbersResponse.class)).thenReturn(expected);

        NumbersResponse result = numberService.list();

        assertEquals(1, result.getNumbers().size());
        assertEquals(3, result.getSlot().getMax());
        verify(httpClient, times(1)).get(BASE, NumbersResponse.class);
    }

    @Test
    @DisplayName("should update a number")
    void shouldUpdateNumber() {
        UpdateNumberRequest request = UpdateNumberRequest.builder().alias("Support").isDefault(true).build();
        Map<String, Object> expected = Map.of("id", "num_1");
        when(httpClient.patch(eq(BASE + "/num_1"), eq(request), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = numberService.update("num_1", request);

        assertEquals("num_1", result.get("id"));
        verify(httpClient, times(1)).patch(eq(BASE + "/num_1"), eq(request), any(TypeReference.class));
    }

    @Test
    @DisplayName("should reject null update request")
    void shouldRejectNullUpdate() {
        assertThrows(AraraException.class, () -> numberService.update("num_1", null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should soft-delete a number")
    void shouldDeleteNumber() {
        numberService.delete("num_1");

        verify(httpClient, times(1)).delete(BASE + "/num_1");
    }

    @Test
    @DisplayName("should request a new dedicated number")
    void shouldRequestNumber() {
        RequestNumberRequest request = RequestNumberRequest.builder()
                .reason("scale")
                .expectedVolume("1000")
                .areaCode("11")
                .build();
        Map<String, Object> expected = Map.of("status", "PENDING");
        when(httpClient.post(eq(BASE + "/request"), eq(request), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = numberService.request(request);

        assertEquals("PENDING", result.get("status"));
        verify(httpClient, times(1)).post(eq(BASE + "/request"), eq(request), any(TypeReference.class));
    }

    @Test
    @DisplayName("should reject null request-number payload")
    void shouldRejectNullRequest() {
        assertThrows(AraraException.class, () -> numberService.request(null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should list pending and resolved number requests")
    void shouldListRequests() {
        List<Map<String, Object>> expected = Arrays.asList(Map.of("id", "req_1"));
        when(httpClient.get(eq(BASE + "/requests"), any(TypeReference.class))).thenReturn(expected);

        List<Map<String, Object>> result = numberService.listRequests();

        assertEquals(1, result.size());
        verify(httpClient, times(1)).get(eq(BASE + "/requests"), any(TypeReference.class));
    }

    @Test
    @DisplayName("should trigger a provider sync with null body")
    void shouldSyncNumber() {
        Map<String, Object> expected = Map.of("synced", true);
        when(httpClient.post(eq(BASE + "/num_1/sync"), isNull(), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = numberService.sync("num_1");

        assertEquals(true, result.get("synced"));
        verify(httpClient, times(1)).post(eq(BASE + "/num_1/sync"), isNull(), any(TypeReference.class));
    }

    @Test
    @DisplayName("should return warming recommendations")
    void shouldReturnWarming() {
        Map<String, Object> expected = Map.of("recommendedDailyCap", 50);
        when(httpClient.get(eq(BASE + "/num_1/warming"), any(TypeReference.class))).thenReturn(expected);

        Map<String, Object> result = numberService.warming("num_1");

        assertNotNull(result);
        assertEquals(50, result.get("recommendedDailyCap"));
        verify(httpClient, times(1)).get(eq(BASE + "/num_1/warming"), any(TypeReference.class));
    }
}
