package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.MessageResponse;
import com.ararahq.arara.sdk.models.SendMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MessageService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService Tests")
class MessageServiceTest {

        @Mock
        private AraraHttpClient httpClient;

        private MessageService messageService;

        @BeforeEach
        void setUp() {
                messageService = new MessageService(httpClient);
        }

        @Nested
        @DisplayName("send() method")
        class SendMessageTests {

                @Test
                @DisplayName("should send message successfully with template")
                void shouldSendMessageWithTemplate() {
                        // Arrange
                        SendMessageRequest request = SendMessageRequest.builder()
                                        .receiver("whatsapp:+5511999998888")
                                        .templateName("hello_world")
                                        .templateVariables(Arrays.asList("John", "Doe"))
                                        .build();

                        MessageResponse expectedResponse = MessageResponse.builder()
                                        .id("msg_123abc")
                                        .status("SENT")
                                        .mode("LIVE")
                                        .sender("whatsapp:+551140001000")
                                        .receiver("whatsapp:+5511999998888")
                                        .body("Hello John Doe")
                                        .cost(BigDecimal.valueOf(0.05))
                                        .build();

                        when(httpClient.post("/v1/messages", request, MessageResponse.class))
                                        .thenReturn(expectedResponse);

                        // Act
                        MessageResponse result = messageService.send(request);

                        // Assert
                        assertNotNull(result);
                        assertEquals("msg_123abc", result.getId());
                        assertEquals("SENT", result.getStatus());
                        assertEquals("whatsapp:+5511999998888", result.getReceiver());
                        assertEquals(BigDecimal.valueOf(0.05), result.getCost());
                        verify(httpClient, times(1)).post("/v1/messages", request, MessageResponse.class);
                }

                @Test
                @DisplayName("should send message successfully with free text")
                void shouldSendMessageWithFreeText() {
                        // Arrange
                        SendMessageRequest request = SendMessageRequest.builder()
                                        .receiver("whatsapp:+5511999998888")
                                        .body("Hello, this is a free text message!")
                                        .build();

                        MessageResponse expectedResponse = MessageResponse.builder()
                                        .id("msg_456def")
                                        .status("SENT")
                                        .mode("LIVE")
                                        .receiver("whatsapp:+5511999998888")
                                        .body("Hello, this is a free text message!")
                                        .cost(BigDecimal.valueOf(0.02))
                                        .build();

                        when(httpClient.post("/v1/messages", request, MessageResponse.class))
                                        .thenReturn(expectedResponse);

                        // Act
                        MessageResponse result = messageService.send(request);

                        // Assert
                        assertNotNull(result);
                        assertEquals("msg_456def", result.getId());
                        assertEquals("Hello, this is a free text message!", result.getBody());
                        assertEquals(BigDecimal.valueOf(0.02), result.getCost());
                }

                @Test
                @DisplayName("should throw exception when request is null")
                void shouldThrowExceptionWhenRequestIsNull() {
                        // Act & Assert
                        assertThrows(AraraException.class, () -> messageService.send(null),
                                        "Should throw AraraException when request is null");
                }

                @Test
                @DisplayName("should throw exception when receiver is null")
                void shouldThrowExceptionWhenReceiverIsNull() {
                        // Arrange
                        SendMessageRequest request = SendMessageRequest.builder()
                                        .receiver(null)
                                        .body("Test message")
                                        .build();

                        // Act & Assert
                        assertThrows(AraraException.class, () -> messageService.send(request),
                                        "Should throw AraraException when receiver is null");
                }

                @Test
                @DisplayName("should throw exception when receiver format is invalid")
                void shouldThrowExceptionWhenReceiverFormatIsInvalid() {
                        // Arrange
                        SendMessageRequest request = SendMessageRequest.builder()
                                        .receiver("invalid-format")
                                        .body("Test message")
                                        .build();

                        // Act & Assert
                        AraraException exception = assertThrows(AraraException.class,
                                        () -> messageService.send(request),
                                        "Should throw AraraException when receiver format is invalid");
                        assertTrue(exception.getMessage().contains("whatsapp:+"));
                }

                @Test
                @DisplayName("should throw exception when receiver doesn't start with whatsapp:")
                void shouldThrowExceptionWhenReceiverDoesntStartWithWhatsappPrefix() {
                        // Arrange
                        SendMessageRequest request = SendMessageRequest.builder()
                                        .receiver("+5511999998888")
                                        .body("Test message")
                                        .build();

                        // Act & Assert
                        assertThrows(AraraException.class, () -> messageService.send(request),
                                        "Should throw AraraException when receiver doesn't start with 'whatsapp:'");
                }

                @Test
                @DisplayName("should send message with all optional fields populated")
                void shouldSendMessageWithAllOptionalFields() {
                        // Arrange
                        SendMessageRequest request = SendMessageRequest.builder()
                                        .receiver("whatsapp:+5511999998888")
                                        .sender("whatsapp:+551140001000")
                                        .templateName("template_1")
                                        .templateVariables(Arrays.asList("var1", "var2"))
                                        .mode("TEST")
                                        .smartLinkParam("campaign_id")
                                        .smartLinkUrl("https://example.com/campaign")
                                        .build();

                        MessageResponse expectedResponse = MessageResponse.builder()
                                        .id("msg_full")
                                        .status("SENT")
                                        .receiver("whatsapp:+5511999998888")
                                        .build();

                        when(httpClient.post("/v1/messages", request, MessageResponse.class))
                                        .thenReturn(expectedResponse);

                        // Act
                        MessageResponse result = messageService.send(request);

                        // Assert
                        assertNotNull(result);
                        assertEquals("msg_full", result.getId());
                        verify(httpClient, times(1)).post(eq("/v1/messages"), eq(request), eq(MessageResponse.class));
                }
        }

        @Nested
        @DisplayName("getById() method")
        class GetByIdTests {

                @Test
                @DisplayName("should retrieve message by ID successfully")
                void shouldRetrieveMessageByIdSuccessfully() {
                        // Arrange
                        String messageId = "msg_123abc";
                        MessageResponse expectedResponse = MessageResponse.builder()
                                        .id(messageId)
                                        .status("DELIVERED")
                                        .receiver("whatsapp:+5511999998888")
                                        .body("Test message")
                                        .build();

                        when(httpClient.get("/v1/messages/" + messageId, MessageResponse.class))
                                        .thenReturn(expectedResponse);

                        // Act
                        MessageResponse result = messageService.getById(messageId);

                        // Assert
                        assertNotNull(result);
                        assertEquals(messageId, result.getId());
                        assertEquals("DELIVERED", result.getStatus());
                        verify(httpClient, times(1)).get("/v1/messages/" + messageId, MessageResponse.class);
                }

                @Test
                @DisplayName("should call correct endpoint with message ID")
                void shouldCallCorrectEndpointWithMessageId() {
                        // Arrange
                        String messageId = "msg_xyz789";
                        MessageResponse mockResponse = MessageResponse.builder()
                                        .id(messageId)
                                        .status("READ")
                                        .build();

                        when(httpClient.get("/v1/messages/" + messageId, MessageResponse.class))
                                        .thenReturn(mockResponse);

                        // Act
                        messageService.getById(messageId);

                        // Assert
                        verify(httpClient, times(1)).get("/v1/messages/" + messageId, MessageResponse.class);
                }

                @Test
                @DisplayName("should handle message with various statuses")
                void shouldHandleMessageWithVariousStatuses() {
                        // Test with different statuses
                        String[] statuses = { "SENT", "DELIVERED", "READ", "FAILED", "PENDING" };

                        for (String status : statuses) {
                                // Arrange
                                String messageId = "msg_" + status;
                                MessageResponse mockResponse = MessageResponse.builder()
                                                .id(messageId)
                                                .status(status)
                                                .receiver("whatsapp:+5511999998888")
                                                .build();

                                when(httpClient.get("/v1/messages/" + messageId, MessageResponse.class))
                                                .thenReturn(mockResponse);

                                // Act
                                MessageResponse result = messageService.getById(messageId);

                                // Assert
                                assertNotNull(result);
                                assertEquals(status, result.getStatus());
                                assertEquals(messageId, result.getId());
                        }
                }

                @Test
                @DisplayName("should retrieve message with cost information")
                void shouldRetrieveMessageWithCostInformation() {
                        // Arrange
                        String messageId = "msg_cost_test";
                        BigDecimal expectedCost = BigDecimal.valueOf(0.15);
                        MessageResponse expectedResponse = MessageResponse.builder()
                                        .id(messageId)
                                        .status("DELIVERED")
                                        .cost(expectedCost)
                                        .build();

                        when(httpClient.get("/v1/messages/" + messageId, MessageResponse.class))
                                        .thenReturn(expectedResponse);

                        // Act
                        MessageResponse result = messageService.getById(messageId);

                        // Assert
                        assertNotNull(result);
                        assertEquals(expectedCost, result.getCost());
                }
        }

        @Nested
        @DisplayName("Integration scenarios")
        class IntegrationScenarios {

                @Test
                @DisplayName("should send then retrieve the same message")
                void shouldSendThenRetrieveSameMessage() {
                        // Arrange
                        String messageId = "msg_integration";
                        SendMessageRequest sendRequest = SendMessageRequest.builder()
                                        .receiver("whatsapp:+5511999998888")
                                        .body("Integration test message")
                                        .build();

                        MessageResponse sentResponse = MessageResponse.builder()
                                        .id(messageId)
                                        .status("SENT")
                                        .receiver("whatsapp:+5511999998888")
                                        .body("Integration test message")
                                        .cost(BigDecimal.valueOf(0.05))
                                        .build();

                        MessageResponse retrievedResponse = MessageResponse.builder()
                                        .id(messageId)
                                        .status("DELIVERED")
                                        .receiver("whatsapp:+5511999998888")
                                        .body("Integration test message")
                                        .cost(BigDecimal.valueOf(0.05))
                                        .build();

                        when(httpClient.post("/v1/messages", sendRequest, MessageResponse.class))
                                        .thenReturn(sentResponse);
                        when(httpClient.get("/v1/messages/" + messageId, MessageResponse.class))
                                        .thenReturn(retrievedResponse);

                        // Act
                        MessageResponse sent = messageService.send(sendRequest);
                        MessageResponse retrieved = messageService.getById(sent.getId());

                        // Assert
                        assertEquals(messageId, sent.getId());
                        assertEquals("SENT", sent.getStatus());
                        assertEquals("DELIVERED", retrieved.getStatus());
                        assertEquals(sent.getId(), retrieved.getId());
                        assertEquals(sent.getCost(), retrieved.getCost());
                }

                @Test
                @DisplayName("should send multiple messages")
                void shouldSendMultipleMessages() {
                        // Arrange
                        String[] receivers = { "whatsapp:+5511999998888", "whatsapp:+5511988887777",
                                        "whatsapp:+5511977776666" };
                        MessageResponse[] responses = new MessageResponse[3];

                        for (int i = 0; i < receivers.length; i++) {
                                SendMessageRequest request = SendMessageRequest.builder()
                                                .receiver(receivers[i])
                                                .body("Test message " + i)
                                                .build();

                                responses[i] = MessageResponse.builder()
                                                .id("msg_" + i)
                                                .status("SENT")
                                                .receiver(receivers[i])
                                                .build();

                                when(httpClient.post("/v1/messages", request, MessageResponse.class))
                                                .thenReturn(responses[i]);
                        }

                        // Act
                        for (int i = 0; i < receivers.length; i++) {
                                SendMessageRequest request = SendMessageRequest.builder()
                                                .receiver(receivers[i])
                                                .body("Test message " + i)
                                                .build();
                                messageService.send(request);
                        }

                        // Assert
                        verify(httpClient, times(3)).post(eq("/v1/messages"), any(SendMessageRequest.class),
                                        eq(MessageResponse.class));
                }
        }
}
