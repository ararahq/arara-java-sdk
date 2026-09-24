package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.models.CreateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.PaginatedResponse;
import com.ararahq.arara.sdk.models.UpdateWhatsAppSmartLinkRequest;
import com.ararahq.arara.sdk.models.WhatsAppSmartLinkResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SmartLinkService against fake API")
class SmartLinkServiceTest extends FakeApi {
    private static final String ID = "5d0f6c7a-2222-4000-8000-000000000003";
    private static final String LINK = "{\"id\":\"" + ID + "\",\"name\":\"Loja\",\"code\":\"abc\",\"clicks\":7}";

    @Test
    @DisplayName("should parse the {data, pagination} envelope of GET /v1/smart-links/whatsapp")
    void shouldListPaginated() throws Exception {
        respond(200, "{\"data\":[" + LINK + "],\"pagination\":{\"page\":0,\"size\":50,"
                + "\"totalElements\":1,\"totalPages\":1}}");

        PaginatedResponse<WhatsAppSmartLinkResponse> page = arara.getSmartLinks().list();

        take("GET", "/v1/smart-links/whatsapp");
        assertEquals(7, page.getData().get(0).getClicks());
        assertEquals(1, page.getPagination().getTotalPages());
    }

    @Test
    @DisplayName("should send page and size")
    void shouldListWithPage() throws Exception {
        respond(200, "{\"data\":[],\"pagination\":{\"page\":2,\"size\":5,\"totalElements\":0,\"totalPages\":0}}");

        arara.getSmartLinks().list(2, 5);

        take("GET", "/v1/smart-links/whatsapp?page=2&size=5");
    }

    @Test
    @DisplayName("should create, update and read stats")
    void shouldCreateUpdateAndStats() throws Exception {
        respond(200, LINK);
        respond(200, LINK);
        respond(200, "{\"clicks\":7}");

        arara.getSmartLinks().create(CreateWhatsAppSmartLinkRequest.builder()
                .name("Loja").phoneNumber("5511999998888").build());
        arara.getSmartLinks().update(ID, UpdateWhatsAppSmartLinkRequest.builder().name("Loja 2").build());
        assertEquals(7, arara.getSmartLinks().stats(ID).get("clicks"));

        assertEquals("5511999998888", json(take("POST", "/v1/smart-links/whatsapp")).get("phoneNumber").asText());
        assertEquals("Loja 2", json(take("PUT", "/v1/smart-links/whatsapp/" + ID)).get("name").asText());
        take("GET", "/v1/smart-links/whatsapp/" + ID + "/stats");
    }
}
