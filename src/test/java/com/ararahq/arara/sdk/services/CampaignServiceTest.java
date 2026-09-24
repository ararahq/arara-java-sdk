package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.Arara;
import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.models.CampaignContactRequest;
import com.ararahq.arara.sdk.models.CampaignDetail;
import com.ararahq.arara.sdk.models.CampaignPage;
import com.ararahq.arara.sdk.models.CampaignRequest;
import com.ararahq.arara.sdk.models.CampaignResponse;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CampaignService against fake API")
class CampaignServiceTest extends FakeApi {
    private static final UUID ID = UUID.fromString("7f1c1c1e-0000-4000-8000-000000000001");
    private static final String CREATED = "{\"id\":\"" + ID + "\",\"name\":\"Black Friday\","
            + "\"status\":\"PROCESSING\",\"totalMessages\":1,\"totalCost\":0.05}";

    private CampaignRequest request(String to) {
        return CampaignRequest.builder()
                .name("Black Friday")
                .templateName("promo")
                .contacts(List.of(CampaignContactRequest.builder().to(to).variables(List.of("Ana")).build()))
                .build();
    }

    @Test
    @DisplayName("should POST /v1/campaigns with contacts in 'to' and a generated Idempotency-Key")
    void shouldCreateCampaign() throws Exception {
        respond(200, CREATED);

        CampaignResponse response = arara.getCampaigns().create(request("5511999998888"));

        RecordedRequest recorded = take("POST", "/v1/campaigns");
        JsonNode body = json(recorded);
        assertEquals("5511999998888", body.get("contacts").get(0).get("to").asText());
        assertNotNull(UUID.fromString(recorded.getHeader("Idempotency-Key")));
        assertEquals(ID, response.getId());
    }

    @Test
    @DisplayName("should use caller key and reuse it on retry after 5xx")
    void shouldReuseCallerKeyOnRetry() throws Exception {
        Arara retrying = client(1);
        respond(500, "{\"error\":{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"x\",\"details\":{}}}");
        respond(200, CREATED);

        retrying.getCampaigns().create(request("+5511999998888"), "camp-1");

        assertEquals("camp-1", take("POST", "/v1/campaigns").getHeader("Idempotency-Key"));
        assertEquals("camp-1", take("POST", "/v1/campaigns").getHeader("Idempotency-Key"));
    }

    @Test
    @DisplayName("should reject invalid contact phone locally")
    void shouldRejectInvalidContact() {
        assertThrows(AraraException.class, () -> arara.getCampaigns().create(request("12")));
        assertThrows(AraraException.class, () -> arara.getCampaigns().create(null));
        assertEquals(0, server.getRequestCount());
    }

    @Test
    @DisplayName("should GET /v1/campaigns and parse {content,totalPages,totalElements}")
    void shouldListCampaigns() throws Exception {
        respond(200, "{\"content\":[{\"id\":\"" + ID + "\",\"name\":\"BF\",\"status\":\"DONE\","
                + "\"templateName\":\"promo\",\"totalMessages\":10,\"sentCount\":10,"
                + "\"scheduledAt\":\"2026-11-27T12:00:00Z\",\"createdAt\":\"2026-11-20T09:00:00Z\"}],"
                + "\"totalPages\":3,\"totalElements\":21}");

        CampaignPage page = arara.getCampaigns().list(0, 10, "DONE");

        take("GET", "/v1/campaigns?page=0&size=10&status=DONE");
        assertEquals(21, page.getTotalElements());
        assertEquals("promo", page.getContent().get(0).getTemplateName());
        assertEquals(Instant.parse("2026-11-27T12:00:00Z"), page.getContent().get(0).getScheduledAt());
        assertEquals(Instant.parse("2026-11-20T09:00:00Z"), page.getContent().get(0).getCreatedAt());
    }

    @Test
    @DisplayName("should GET /v1/campaigns/{id} and parse the full detail")
    void shouldGetCampaignDetail() throws Exception {
        respond(200, "{\"id\":\"" + ID + "\",\"name\":\"BF\",\"status\":\"DONE\",\"templateName\":\"promo\","
                + "\"totalMessages\":10,\"sentCount\":9,\"deliveredCount\":8,\"readCount\":5,\"failedCount\":1,"
                + "\"clickedCount\":3,\"convertedCount\":2,\"convertedValue\":199.90,\"blockedCount\":1,"
                + "\"blockReasons\":[{\"motivo\":\"Pediu para sair\",\"quantidade\":1}],\"refundCount\":1,"
                + "\"refundValue\":0.05,\"totalCost\":0.45,\"startedAt\":\"2026-11-27T12:00:00Z\","
                + "\"finishedAt\":\"2026-11-27T12:05:00Z\"}");

        CampaignDetail detail = arara.getCampaigns().getById(ID);

        take("GET", "/v1/campaigns/" + ID);
        assertEquals("promo", detail.getTemplateName());
        assertEquals(8, detail.getDeliveredCount());
        assertEquals(3, detail.getClickedCount());
        assertEquals(2, detail.getConvertedCount());
        assertEquals("Pediu para sair", detail.getBlockReasons().get(0).getMotivo());
        assertEquals(new BigDecimal("0.05"), detail.getRefundValue());
        assertEquals(Instant.parse("2026-11-27T12:05:00Z"), detail.getFinishedAt());
    }

    @Test
    @DisplayName("should parse scheduledAt on create response")
    void shouldParseScheduledAt() throws Exception {
        respond(200, "{\"id\":\"" + ID + "\",\"status\":\"SCHEDULED\",\"scheduledAt\":\"2026-11-27T12:00:00Z\"}");

        CampaignResponse response = arara.getCampaigns().create(request("5511999998888"));

        take("POST", "/v1/campaigns");
        assertEquals(Instant.parse("2026-11-27T12:00:00Z"), response.getScheduledAt());
    }

    @Test
    @DisplayName("should reject a null contact with its index")
    void shouldRejectNullContact() {
        CampaignRequest request = CampaignRequest.builder().name("x").templateName("t")
                .contacts(Arrays.asList(CampaignContactRequest.builder().to("5511999998888").build(), null))
                .build();

        AraraException error = assertThrows(AraraException.class, () -> arara.getCampaigns().create(request));

        assertTrue(error.getMessage().contains("contacts[1]"));
        assertEquals(0, server.getRequestCount());
    }

    @Test
    @DisplayName("should POST /v1/campaigns/{id}/cancel without retrying")
    void shouldCancelCampaign() throws Exception {
        Arara retrying = client(2);
        respond(500, "");

        assertThrows(AraraApiException.class, () -> retrying.getCampaigns().cancel(ID));

        take("POST", "/v1/campaigns/" + ID + "/cancel");
        assertEquals(1, server.getRequestCount());
    }

    @Test
    @DisplayName("should cancel successfully on 200")
    void shouldCancelSuccessfully() throws Exception {
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(200));

        arara.getCampaigns().cancel(ID);

        take("POST", "/v1/campaigns/" + ID + "/cancel");
    }
}
