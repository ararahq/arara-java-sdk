package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.models.RequestNumberRequest;
import com.ararahq.arara.sdk.models.UpdateNumberRequest;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("NumberService against fake API")
class NumberServiceTest extends FakeApi {
    private static final String BASE = "/v1/organizations/me/numbers";

    @Test
    @DisplayName("should hit every numbers endpoint with the right method and path")
    void shouldCoverNumbers() throws Exception {
        respond(200, "{\"numbers\":[],\"slot\":null}");
        respond(200, "{\"ok\":true}");
        server.enqueue(new MockResponse().setResponseCode(204));
        respond(200, "{\"requested\":true}");
        respond(200, "[{\"id\":\"r1\"}]");
        respond(200, "{\"synced\":true}");
        respond(200, "{\"dailyLimit\":250}");

        arara.getNumbers().list();
        arara.getNumbers().update("n1", UpdateNumberRequest.builder().alias("Vendas").build());
        arara.getNumbers().delete("n1");
        arara.getNumbers().request(RequestNumberRequest.builder().areaCode("11").build());
        assertEquals("r1", arara.getNumbers().listRequests().get(0).get("id"));
        arara.getNumbers().sync("n1");
        assertEquals(250, arara.getNumbers().warming("n1").get("dailyLimit"));

        take("GET", BASE);
        assertEquals("Vendas", json(take("PATCH", BASE + "/n1")).get("alias").asText());
        take("DELETE", BASE + "/n1");
        assertEquals("11", json(take("POST", BASE + "/request")).get("areaCode").asText());
        take("GET", BASE + "/requests");
        take("POST", BASE + "/n1/sync");
        take("GET", BASE + "/n1/warming");
    }
}
