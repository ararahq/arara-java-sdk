package com.ararahq.arara.sdk.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OptOutService against fake API")
class OptOutServiceTest extends FakeApi {

    @Test
    @DisplayName("should list, add, get and remove opt-outs")
    void shouldCoverOptOuts() throws Exception {
        respond(200, "{\"items\":[],\"total\":0}");
        respond(201, "{\"phone\":\"+5511999998888\"}");
        respond(200, "{\"optedOut\":true}");
        respond(200, "{\"removed\":true}");

        assertEquals(0, arara.getOptOuts().list().get("total"));
        arara.getOptOuts().add("+5511999998888", "pediu");
        assertEquals(true, arara.getOptOuts().get("+5511999998888").get("optedOut"));
        assertEquals(true, arara.getOptOuts().remove("+5511999998888").get("removed"));

        take("GET", "/v1/opt-outs");
        JsonNode body = json(take("POST", "/v1/opt-outs"));
        assertEquals("+5511999998888", body.get("phone").asText());
        assertEquals("pediu", body.get("reason").asText());
        take("GET", "/v1/opt-outs/%2B5511999998888");
        take("DELETE", "/v1/opt-outs/%2B5511999998888");
    }

    @Test
    @DisplayName("should reject null phone locally")
    void shouldRejectNullPhone() {
        assertThrows(RuntimeException.class, () -> arara.getOptOuts().add(null, null));
        assertThrows(RuntimeException.class, () -> arara.getOptOuts().get(null));
        assertThrows(RuntimeException.class, () -> arara.getOptOuts().remove(null));
        assertEquals(0, server.getRequestCount());
    }
}
