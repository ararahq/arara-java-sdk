package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CampaignContactRequest;
import com.ararahq.arara.sdk.models.CampaignRequest;
import com.ararahq.arara.sdk.models.CampaignResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CampaignService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CampaignService Tests")
class CampaignServiceTest {
        @Mock
        private AraraHttpClient httpClient;
        private CampaignService campaignService;

        @BeforeEach
        void setUp() {
                campaignService = new CampaignService(httpClient);
        }

        @Test
        @DisplayName("should create campaign successfully")
        void shouldCreateCampaignSuccessfully() {
                // Arrange
                CampaignContactRequest contact = CampaignContactRequest.builder()
                                .to("whatsapp:+5511999998888")
                                .build();
                CampaignRequest request = CampaignRequest.builder()
                                .name("Birthday Campaign")
                                .templateName("birthday_greeting")
                                .sender("whatsapp:+551140001000")
                                .contacts(Arrays.asList(contact))
                                .build();
                UUID campaignId = UUID.randomUUID();
                CampaignResponse expectedResponse = CampaignResponse.builder()
                                .id(campaignId)
                                .name("Birthday Campaign")
                                .status("ACTIVE")
                                .totalMessages(1)
                                .totalCost(BigDecimal.valueOf(0.05))
                                .build();
                when(httpClient.post("/v1/campaigns", request, CampaignResponse.class))
                                .thenReturn(expectedResponse);
                // Act
                CampaignResponse result = campaignService.create(request);
                // Assert
                assertNotNull(result);
                assertEquals(campaignId, result.getId());
                assertEquals("Birthday Campaign", result.getName());
                assertEquals("ACTIVE", result.getStatus());
                verify(httpClient, times(1)).post("/v1/campaigns", request, CampaignResponse.class);
        }

        @Test
        @DisplayName("should create campaign with multiple contacts")
        void shouldCreateCampaignWithMultipleContacts() {
                // Arrange
                CampaignContactRequest contact1 = CampaignContactRequest.builder()
                                .to("whatsapp:+5511999998888")
                                .build();
                CampaignContactRequest contact2 = CampaignContactRequest.builder()
                                .to("whatsapp:+5511988887777")
                                .build();
                CampaignRequest request = CampaignRequest.builder()
                                .name("Newsletter Campaign")
                                .templateName("newsletter_template")
                                .sender("whatsapp:+551140001000")
                                .contacts(Arrays.asList(contact1, contact2))
                                .build();
                UUID campaignId = UUID.randomUUID();
                CampaignResponse expectedResponse = CampaignResponse.builder()
                                .id(campaignId)
                                .name("Newsletter Campaign")
                                .status("ACTIVE")
                                .totalMessages(2)
                                .totalCost(BigDecimal.valueOf(0.10))
                                .build();
                when(httpClient.post("/v1/campaigns", request, CampaignResponse.class))
                                .thenReturn(expectedResponse);
                // Act
                CampaignResponse result = campaignService.create(request);
                // Assert
                assertNotNull(result);
                assertEquals(2, result.getTotalMessages());
                assertEquals(BigDecimal.valueOf(0.10), result.getTotalCost());
        }

        @Test
        @DisplayName("should retrieve campaign by ID successfully")
        void shouldRetrieveCampaignByIdSuccessfully() {
                // Arrange
                UUID campaignId = UUID.randomUUID();
                CampaignResponse expectedResponse = CampaignResponse.builder()
                                .id(campaignId)
                                .name("Test Campaign")
                                .status("ACTIVE")
                                .totalMessages(100)
                                .totalCost(BigDecimal.valueOf(5.00))
                                .build();
                when(httpClient.get("/v1/campaigns/" + campaignId.toString(), CampaignResponse.class))
                                .thenReturn(expectedResponse);
                // Act
                CampaignResponse result = campaignService.getById(campaignId);
                // Assert
                assertNotNull(result);
                assertEquals(campaignId, result.getId());
                assertEquals("Test Campaign", result.getName());
                assertEquals("ACTIVE", result.getStatus());
                verify(httpClient, times(1)).get("/v1/campaigns/" + campaignId.toString(), CampaignResponse.class);
        }

        @Test
        @DisplayName("should call correct endpoint with UUID")
        void shouldCallCorrectEndpointWithUUID() {
                // Arrange
                UUID campaignId = UUID.randomUUID();
                CampaignResponse mockResponse = CampaignResponse.builder()
                                .id(campaignId)
                                .name("Test")
                                .status("ACTIVE")
                                .totalMessages(1)
                                .totalCost(BigDecimal.ZERO)
                                .build();
                when(httpClient.get("/v1/campaigns/" + campaignId.toString(), CampaignResponse.class))
                                .thenReturn(mockResponse);
                // Act
                campaignService.getById(campaignId);
                // Assert
                verify(httpClient, times(1)).get("/v1/campaigns/" + campaignId.toString(), CampaignResponse.class);
        }

        @Test
        @DisplayName("should create then retrieve campaign")
        void shouldCreateThenRetrieveCampaign() {
                // Arrange
                UUID campaignId = UUID.randomUUID();
                CampaignRequest createRequest = CampaignRequest.builder()
                                .name("Integration Campaign")
                                .templateName("template")
                                .sender("whatsapp:+551140001000")
                                .contacts(Arrays.asList(
                                                CampaignContactRequest.builder()
                                                                .to("whatsapp:+5511999998888")
                                                                .build()))
                                .build();
                CampaignResponse createResponse = CampaignResponse.builder()
                                .id(campaignId)
                                .name("Integration Campaign")
                                .status("ACTIVE")
                                .totalMessages(1)
                                .totalCost(BigDecimal.valueOf(0.05))
                                .build();
                when(httpClient.post("/v1/campaigns", createRequest, CampaignResponse.class))
                                .thenReturn(createResponse);
                when(httpClient.get("/v1/campaigns/" + campaignId.toString(), CampaignResponse.class))
                                .thenReturn(createResponse);
                // Act
                CampaignResponse created = campaignService.create(createRequest);
                CampaignResponse retrieved = campaignService.getById(created.getId());
                // Assert
                assertEquals(campaignId, created.getId());
                assertEquals(created.getId(), retrieved.getId());
        }
}
