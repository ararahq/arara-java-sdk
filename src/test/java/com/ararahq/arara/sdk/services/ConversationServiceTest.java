package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.models.ConversationReplyRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ConversationService against fake API")
class ConversationServiceTest extends FakeApi {
    private static final String CONV = "c0ffee00-3333-4000-8000-000000000004";

    @Test
    @DisplayName("should list with encoded filters and read lead stats")
    void shouldListAndStats() throws Exception {
        respond(200, "{\"content\":[],\"total\":0}");
        respond(200, "{\"HOT\":2}");
        respond(200, "{\"content\":[]}");

        arara.getConversations().list("OPEN", "HOT & WARM", 0, 20);
        assertEquals(2, arara.getConversations().leadStats().get("HOT"));
        arara.getConversations().list(null, null, 1, 5);

        take("GET", "/v1/conversations?page=0&size=20&status=OPEN&leadStatus=HOT+%26+WARM");
        take("GET", "/v1/conversations/lead-stats");
        take("GET", "/v1/conversations?page=1&size=5");
    }

    @Test
    @DisplayName("should read messages, reply, update status and check window")
    void shouldCoverConversationActions() throws Exception {
        respond(200, "{\"messages\":[]}");
        respond(200, "{\"ok\":true}");
        respond(200, "{\"status\":\"CLOSED\"}");
        respond(200, "{\"5511999998888\":\"OPEN\"}");

        arara.getConversations().messages(CONV, 0, 50);
        arara.getConversations().reply(ConversationReplyRequest.builder()
                .conversationId(UUID.fromString(CONV)).body("Oi").build());
        arara.getConversations().updateStatus(CONV, "CLOSED");
        assertEquals("OPEN", arara.getConversations().windowStatus(List.of("5511999998888")).get("5511999998888"));

        take("GET", "/v1/conversations/" + CONV + "/messages?page=0&size=50");
        assertEquals("Oi", json(take("POST", "/v1/conversations/reply")).get("body").asText());
        assertEquals("CLOSED", json(take("PATCH", "/v1/conversations/" + CONV + "/status")).get("status").asText());
        assertEquals("5511999998888",
                json(take("POST", "/v1/conversations/window-status")).get("phones").get(0).asText());
    }
}
