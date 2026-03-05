package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.CreateTemplateRequest;
import com.ararahq.arara.sdk.models.TemplateResponse;
import com.ararahq.arara.sdk.models.TemplateStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateService Tests")
class TemplateServiceTest {

    @Mock
    private AraraHttpClient httpClient;

    private TemplateService templateService;

    @BeforeEach
    void setUp() {
        templateService = new TemplateService(httpClient);
    }

    @Test
    @DisplayName("should create template")
    void shouldCreateTemplate() {
        CreateTemplateRequest request = CreateTemplateRequest.builder()
                .name("test_template")
                .body("Hello")
                .category("MARKETING")
                .build();

        TemplateResponse expected = TemplateResponse.builder().id(UUID.randomUUID()).build();
        when(httpClient.post("/v1/templates", request, TemplateResponse.class)).thenReturn(expected);

        TemplateResponse result = templateService.create(request);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("should list templates")
    void shouldListTemplates() {
        TemplateResponse[] templates = { TemplateResponse.builder().id(UUID.randomUUID()).build() };
        when(httpClient.get("/v1/templates", TemplateResponse[].class)).thenReturn(templates);

        List<TemplateResponse> result = templateService.list();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("should get template by ID")
    void shouldGetTemplateById() {
        UUID id = UUID.randomUUID();
        TemplateResponse expected = TemplateResponse.builder().id(id).build();
        when(httpClient.get("/v1/templates/" + id, TemplateResponse.class)).thenReturn(expected);

        TemplateResponse result = templateService.getById(id);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("should delete template")
    void shouldDeleteTemplate() {
        UUID id = UUID.randomUUID();
        templateService.delete(id);
        verify(httpClient).delete("/v1/templates/" + id);
    }

    @Test
    @DisplayName("should get template status")
    void shouldGetTemplateStatus() {
        UUID id = UUID.randomUUID();
        TemplateStatusResponse expected = TemplateStatusResponse.builder().status("APPROVED").build();
        when(httpClient.get("/v1/templates/" + id + "/status", TemplateStatusResponse.class)).thenReturn(expected);

        TemplateStatusResponse result = templateService.getStatus(id);
        assertEquals(expected, result);
    }
}
