package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.Arara;
import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.models.CampaignContactRequest;
import com.ararahq.arara.sdk.models.CampaignPage;
import com.ararahq.arara.sdk.models.CampaignRequest;
import com.ararahq.arara.sdk.models.CampaignResponse;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                + "\"templateName\":\"promo\",\"totalMessages\":10,\"sentCount\":10}],"
                + "\"totalPages\":3,\"totalElements\":21}");

        CampaignPage page = arara.getCampaigns().list(0, 10, "DONE");

        take("GET", "/v1/campaigns?page=0&size=10&status=DONE");
        assertEquals(21, page.getTotalElements());
        assertEquals("promo", page.getContent().get(0).getTemplateName());
    }

    @Test
    @DisplayName("should GET /v1/campaigns/{id}")
    void shouldGetCampaign() throws Exception {
        respond(200, CREATED);

        assertEquals("Black Friday", arara.getCampaigns().getById(ID).getName());

        take("GET", "/v1/campaigns/" + ID);
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
