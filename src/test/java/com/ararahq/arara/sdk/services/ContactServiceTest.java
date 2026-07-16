package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.ContactMessagesResponse;
import com.ararahq.arara.sdk.models.ContactPatchRequest;
import com.ararahq.arara.sdk.models.ContactRequest;
import com.ararahq.arara.sdk.models.ContactResponse;
import com.ararahq.arara.sdk.models.ContactsBatchResponse;
import com.ararahq.arara.sdk.models.ContactsListResponse;
import com.ararahq.arara.sdk.models.ContactsReactivationResponse;
import com.ararahq.arara.sdk.models.ContactsStatsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContactService Tests")
class ContactServiceTest {

    @Mock
    private AraraHttpClient httpClient;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = new ContactService(httpClient);
    }

    @Test
    @DisplayName("should list contacts with base pagination when filters are null")
    void shouldListContactsWithoutFilters() {
        ContactsListResponse expected = ContactsListResponse.builder()
                .total(0)
                .page(0)
                .size(20)
                .build();
        when(httpClient.get("/v1/contacts?page=0&size=20", ContactsListResponse.class))
                .thenReturn(expected);

        ContactsListResponse result = contactService.list(0, 20, null, null);

        assertNotNull(result);
        verify(httpClient, times(1)).get("/v1/contacts?page=0&size=20", ContactsListResponse.class);
    }

    @Test
    @DisplayName("should append query and lifecycle filters to the path")
    void shouldListContactsWithFilters() {
        String path = "/v1/contacts?page=1&size=10&q=john&lifecycle=engaged";
        ContactsListResponse expected = ContactsListResponse.builder().total(5).build();
        when(httpClient.get(path, ContactsListResponse.class)).thenReturn(expected);

        ContactsListResponse result = contactService.list(1, 10, "john", "engaged");

        assertEquals(5, result.getTotal());
        verify(httpClient, times(1)).get(path, ContactsListResponse.class);
    }

    @Test
    @DisplayName("should import a batch of contacts")
    void shouldImportBatch() {
        List<ContactRequest> contacts = Arrays.asList(
                ContactRequest.builder().name("Ana").phone("+5511999998888").build());
        ContactsBatchResponse expected = ContactsBatchResponse.builder()
                .importId(UUID.randomUUID())
                .created(1)
                .build();
        when(httpClient.post("/v1/contacts/batch", contacts, ContactsBatchResponse.class))
                .thenReturn(expected);

        ContactsBatchResponse result = contactService.importBatch(contacts);

        assertEquals(1, result.getCreated());
        verify(httpClient, times(1)).post("/v1/contacts/batch", contacts, ContactsBatchResponse.class);
    }

    @Test
    @DisplayName("should reject null batch import")
    void shouldRejectNullBatch() {
        assertThrows(AraraException.class, () -> contactService.importBatch(null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should return aggregate contact stats")
    void shouldReturnStats() {
        ContactsStatsResponse expected = ContactsStatsResponse.builder()
                .total(100)
                .engaged(40)
                .build();
        when(httpClient.get("/v1/contacts/stats", ContactsStatsResponse.class)).thenReturn(expected);

        ContactsStatsResponse result = contactService.stats();

        assertEquals(100, result.getTotal());
        verify(httpClient, times(1)).get("/v1/contacts/stats", ContactsStatsResponse.class);
    }

    @Test
    @DisplayName("should list reactivation candidates with limit")
    void shouldListReactivationCandidates() {
        ContactsReactivationResponse expected = ContactsReactivationResponse.builder().total(3).build();
        when(httpClient.get("/v1/contacts/reactivation?limit=25", ContactsReactivationResponse.class))
                .thenReturn(expected);

        ContactsReactivationResponse result = contactService.reactivationCandidates(25);

        assertEquals(3, result.getTotal());
        verify(httpClient, times(1))
                .get("/v1/contacts/reactivation?limit=25", ContactsReactivationResponse.class);
    }

    @Test
    @DisplayName("should list distinct contact tags")
    void shouldListTags() {
        Map<String, List<String>> expected = Map.of("tags", Arrays.asList("vip", "lead"));
        when(httpClient.get(eq("/v1/contacts/tags"), any(TypeReference.class))).thenReturn(expected);

        Map<String, List<String>> result = contactService.listTags();

        assertEquals(Arrays.asList("vip", "lead"), result.get("tags"));
        verify(httpClient, times(1)).get(eq("/v1/contacts/tags"), any(TypeReference.class));
    }

    @Test
    @DisplayName("should retrieve a contact by phone")
    void shouldGetContactByPhone() {
        ContactResponse expected = ContactResponse.builder()
                .id(UUID.randomUUID())
                .phone("+5511999998888")
                .name("Ana")
                .build();
        when(httpClient.get("/v1/contacts/+5511999998888", ContactResponse.class)).thenReturn(expected);

        ContactResponse result = contactService.get("+5511999998888");

        assertEquals("Ana", result.getName());
        verify(httpClient, times(1)).get("/v1/contacts/+5511999998888", ContactResponse.class);
    }

    @Test
    @DisplayName("should patch a contact by phone")
    void shouldUpdateContact() {
        ContactPatchRequest request = ContactPatchRequest.builder()
                .name("Ana Maria")
                .tags(Arrays.asList("vip"))
                .build();
        ContactResponse expected = ContactResponse.builder().name("Ana Maria").build();
        when(httpClient.patch("/v1/contacts/+5511999998888", request, ContactResponse.class))
                .thenReturn(expected);

        ContactResponse result = contactService.update("+5511999998888", request);

        assertEquals("Ana Maria", result.getName());
        verify(httpClient, times(1))
                .patch("/v1/contacts/+5511999998888", request, ContactResponse.class);
    }

    @Test
    @DisplayName("should reject null patch request")
    void shouldRejectNullUpdate() {
        assertThrows(AraraException.class, () -> contactService.update("+5511999998888", null));
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("should list recent messages for a contact")
    void shouldListContactMessages() {
        ContactMessagesResponse expected = ContactMessagesResponse.builder()
                .phone("+5511999998888")
                .total(2)
                .build();
        when(httpClient.get("/v1/contacts/+5511999998888/messages?limit=50", ContactMessagesResponse.class))
                .thenReturn(expected);

        ContactMessagesResponse result = contactService.messages("+5511999998888", 50);

        assertEquals(2, result.getTotal());
        verify(httpClient, times(1))
                .get("/v1/contacts/+5511999998888/messages?limit=50", ContactMessagesResponse.class);
    }
}
