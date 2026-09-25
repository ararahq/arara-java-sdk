package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.models.BusinessProfilePatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("OrganizationService against fake API")
class OrganizationServiceTest extends FakeApi {
    private static final String PROFILE = "/v1/organizations/me/business-profile";

    @Test
    @DisplayName("should read and patch the business profile and read and change the plan")
    void shouldCoverOrganization() throws Exception {
        respond(200, "{\"displayName\":\"Loja\"}");
        respond(200, "{\"displayName\":\"Loja 2\"}");
        respond(200, "{\"current\":\"VOO\",\"monthlyPriceCents\":99700}");
        respond(200, "{\"plan\":\"DECOLAGEM\"}");

        assertEquals("Loja", arara.getOrganizations().me().getDisplayName());
        arara.getOrganizations().updateBusinessProfile(BusinessProfilePatch.builder().displayName("Loja 2").build());
        assertEquals("VOO", arara.getOrganizations().getPlan().getCurrent());
        assertEquals("DECOLAGEM", arara.getOrganizations().changePlan("DECOLAGEM").get("plan"));

        take("GET", PROFILE);
        assertEquals("Loja 2", json(take("PATCH", PROFILE)).get("displayName").asText());
        take("GET", "/v1/organizations/me/plan");
        assertEquals("DECOLAGEM", json(take("PATCH", "/v1/organizations/me/plan")).get("plan").asText());
    }
}
