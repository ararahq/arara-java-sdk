package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.models.CreateTemplateRequest;
import com.ararahq.arara.sdk.models.PaginatedResponse;
import com.ararahq.arara.sdk.models.TemplateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("TemplateService against fake API")
class TemplateServiceTest extends FakeApi {
    private static final UUID ID = UUID.fromString("0b7e3a8e-1111-4000-8000-000000000002");
    private static final String TEMPLATE = "{\"id\":\"" + ID + "\",\"name\":\"boas_vindas\",\"category\":\"UTILITY\"}";

    @Test
    @DisplayName("should parse the {data, pagination} envelope of GET /v1/templates")
    void shouldListPaginated() throws Exception {
        respond(200, "{\"data\":[" + TEMPLATE + "],\"pagination\":{\"page\":0,\"size\":50,"
                + "\"totalElements\":1,\"totalPages\":1}}");

        PaginatedResponse<TemplateResponse> page = arara.getTemplates().list();

        take("GET", "/v1/templates");
        assertEquals(ID, page.getData().get(0).getId());
        assertEquals(1, page.getPagination().getTotalElements());
        assertEquals(50, page.getPagination().getSize());
    }

    @Test
    @DisplayName("should send name, status, page and size filters encoded")
    void shouldListWithFilters() throws Exception {
        respond(200, "{\"data\":[],\"pagination\":{\"page\":1,\"size\":10,\"totalElements\":0,\"totalPages\":0}}");

        arara.getTemplates().list("boas vindas", "APPROVED", 1, 10);

        take("GET", "/v1/templates?name=boas+vindas&status=APPROVED&page=1&size=10");
    }

    @Test
    @DisplayName("should GET, status and DELETE by template id (UUID), never by name")
    void shouldUseIdInPath() throws Exception {
        respond(200, TEMPLATE);
        respond(200, "{\"status\":\"APPROVED\",\"category\":\"UTILITY\"}");
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"deleted\":true}"));

        assertEquals("boas_vindas", arara.getTemplates().getById(ID).getName());
        assertEquals("APPROVED", arara.getTemplates().getStatus(ID).getStatus());
        arara.getTemplates().delete(ID);

        take("GET", "/v1/templates/" + ID);
        take("GET", "/v1/templates/" + ID + "/status");
        take("DELETE", "/v1/templates/" + ID);
    }

    @Test
    @DisplayName("should reject null id locally")
    void shouldRejectNullId() {
        assertThrows(RuntimeException.class, () -> arara.getTemplates().getById(null));
        assertThrows(RuntimeException.class, () -> arara.getTemplates().getStatus(null));
        assertThrows(RuntimeException.class, () -> arara.getTemplates().delete(null));
        assertThrows(RuntimeException.class, () -> arara.getTemplates().analytics(null, "7d"));
        assertEquals(0, server.getRequestCount());
    }

    @Test
    @DisplayName("should POST /v1/templates with body, language and headerType")
    void shouldCreateTemplate() throws Exception {
        respond(201, TEMPLATE);

        arara.getTemplates().create(CreateTemplateRequest.builder()
                .name("boas_vindas")
                .category("UTILITY")
                .body("Oi {{1}}")
                .headerType("IMAGE")
                .samples(Map.of("1", "Ana"))
                .build());

        JsonNode body = json(take("POST", "/v1/templates"));
        assertEquals("Oi {{1}}", body.get("body").asText());
        assertEquals("pt_BR", body.get("language").asText());
        assertEquals("IMAGE", body.get("headerType").asText());
    }

    @Test
    @DisplayName("should GET analytics globally and per template")
    void shouldGetAnalytics() throws Exception {
        respond(200, "{\"sent\":10}");
        respond(200, "{\"sent\":3}");

        assertEquals(10, arara.getTemplates().analytics("7d").get("sent"));
        assertEquals(3, arara.getTemplates().analytics(ID, null).get("sent"));

        take("GET", "/v1/templates/analytics?period=7d");
        take("GET", "/v1/templates/" + ID + "/analytics");
    }

    @Test
    @DisplayName("should surface 400 INVALID_PATH_PARAM as typed error")
    void shouldSurfaceInvalidPathParam() {
        respond(400, "{\"error\":{\"code\":\"INVALID_PATH_PARAM\",\"message\":\"id\",\"details\":{}}}");

        AraraApiException error = assertThrows(AraraApiException.class, () -> arara.getTemplates().getById(ID));

        assertEquals("INVALID_PATH_PARAM", error.getCode());
    }
}
