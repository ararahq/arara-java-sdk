package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.BusinessProfilePatch;
import com.ararahq.arara.sdk.models.BusinessProfileResponse;
import com.ararahq.arara.sdk.models.OrganizationPlanResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
@DisplayName("OrganizationService Tests")
class OrganizationServiceTest {

    @Mock
    private AraraHttpClient httpClient;

    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        organizationService = new OrganizationService(httpClient);
    }

    @Test
    @DisplayName("should return the organization business profile")
    void shouldReturnBusinessProfile() {
        BusinessProfileResponse expected = BusinessProfileResponse.builder()
                .id(UUID.randomUUID())
                .displayName("Arara")
                .build();
        when(httpClient.get("/v1/organizations/me/business-profile", BusinessProfileResponse.class))
                .thenReturn(expected);

        BusinessProfileResponse result = organizationService.me();

        assertEquals("Arara", result.getDisplayName());
        verify(httpClient, times(1))
                .get("/v1/organizations/me/business-profile", BusinessProfileResponse.class);
    }

    @Test
    @DisplayName("should patch the organization business profile")
    void shouldUpdateBusinessProfile() {
        BusinessProfilePatch request = BusinessProfilePatch.builder()
                .displayName("Arara HQ")
                .vertical("TECH")
                .build();
        BusinessProfileResponse expected = BusinessProfileResponse.builder().displayName("Arara HQ").build();
        when(httpClient.patch("/v1/organizations/me/business-profile", request, BusinessProfileResponse.class))
                .thenReturn(expected);

        BusinessProfileResponse result = organizationService.updateBusinessProfile(request);

        assertEquals("Arara HQ", result.getDisplayName());
        verify(httpClient, times(1))
                .patch("/v1/organizations/me/business-profile", request, BusinessProfileResponse.class);
    }

    @Test
    @DisplayName("should reject null business profile patch")
    void shouldRejectNullPatch() {
        assertThrows(AraraException.class, () -> organizationService.updateBusinessProfile(null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should return the organization current plan")
    void shouldReturnPlan() {
        OrganizationPlanResponse expected = OrganizationPlanResponse.builder()
                .current("VOO")
                .monthlyPriceCents(34900)
                .build();
        when(httpClient.get("/v1/organizations/me/plan", OrganizationPlanResponse.class)).thenReturn(expected);

        OrganizationPlanResponse result = organizationService.getPlan();

        assertEquals("VOO", result.getCurrent());
        verify(httpClient, times(1)).get("/v1/organizations/me/plan", OrganizationPlanResponse.class);
    }

    @Test
    @DisplayName("should change the organization plan")
    void shouldChangePlan() {
        Map<String, Object> expected = Map.of("current", "DECOLAGEM");
        when(httpClient.patch(
                eq("/v1/organizations/me/plan"), eq(Map.of("plan", "DECOLAGEM")), any(TypeReference.class)))
                .thenReturn(expected);

        Map<String, Object> result = organizationService.changePlan("DECOLAGEM");

        assertNotNull(result);
        assertEquals("DECOLAGEM", result.get("current"));
        verify(httpClient, times(1)).patch(
                eq("/v1/organizations/me/plan"), eq(Map.of("plan", "DECOLAGEM")), any(TypeReference.class));
    }
}
