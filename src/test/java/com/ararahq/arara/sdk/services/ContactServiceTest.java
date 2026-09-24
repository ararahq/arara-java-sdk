package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.models.ContactPatchRequest;
import com.ararahq.arara.sdk.models.ContactRequest;
import com.ararahq.arara.sdk.models.ContactsListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ContactService against fake API")
class ContactServiceTest extends FakeApi {
    private static final String CONTACT = "{\"name\":\"Ana\",\"phone\":\"+5511999998888\",\"lifecycle\":\"ENGAGED\"}";

    @Test
    @DisplayName("should URL-encode q and lifecycle on GET /v1/contacts")
    void shouldEncodeQuery() throws Exception {
        respond(200, "{\"contacts\":[" + CONTACT + "],\"total\":1,\"page\":0,\"size\":20,\"totalPages\":1}");

        ContactsListResponse page = arara.getContacts().list(0, 20, "a&b #1", "ENGAGED");

        take("GET", "/v1/contacts?page=0&size=20&q=a%26b+%231&lifecycle=ENGAGED");
        assertEquals("Ana", page.getContacts().get(0).getName());
    }

    @Test
    @DisplayName("should omit null filters")
    void shouldOmitNullFilters() throws Exception {
        respond(200, "{\"contacts\":[],\"total\":0}");

        arara.getContacts().list(1, 5, null, null);

        take("GET", "/v1/contacts?page=1&size=5");
    }

    @Test
    @DisplayName("should encode the phone path segment on get, update and messages")
    void shouldEncodePhonePath() throws Exception {
        respond(200, CONTACT);
        respond(200, CONTACT);
        respond(200, "{\"phone\":\"+5511999998888\",\"total\":0,\"messages\":[]}");

        arara.getContacts().get("+5511999998888");
        arara.getContacts().update("+5511999998888", ContactPatchRequest.builder().tags(List.of("vip")).build());
        arara.getContacts().messages("+5511999998888", 10);

        take("GET", "/v1/contacts/%2B5511999998888");
        assertEquals("vip", json(take("PATCH", "/v1/contacts/%2B5511999998888")).get("tags").get(0).asText());
        take("GET", "/v1/contacts/%2B5511999998888/messages?limit=10");
    }

    @Test
    @DisplayName("should import batch, read stats, reactivation and tags")
    void shouldCoverAggregates() throws Exception {
        respond(200, "{\"created\":1,\"updated\":0,\"skipped\":0,\"errors\":[]}");
        respond(200, "{\"total\":10,\"engaged\":4}");
        respond(200, "{\"total\":1,\"candidates\":[]}");
        respond(200, "{\"tags\":[\"vip\"]}");

        assertEquals(1, arara.getContacts().importBatch(List.of(ContactRequest.builder()
                .name("Ana").phone("5511999998888").attributes(Map.of()).build())).getCreated());
        assertEquals(4, arara.getContacts().stats().getEngaged());
        assertEquals(1, arara.getContacts().reactivationCandidates(50).getTotal());
        assertEquals(List.of("vip"), arara.getContacts().listTags().get("tags"));

        assertEquals("Ana", json(take("POST", "/v1/contacts/batch")).get(0).get("name").asText());
        take("GET", "/v1/contacts/stats");
        take("GET", "/v1/contacts/reactivation?limit=50");
        take("GET", "/v1/contacts/tags");
    }
}
