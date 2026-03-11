package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.UpdateUserRequest;
import com.ararahq.arara.sdk.models.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

        @Mock
        private AraraHttpClient httpClient;

        private UserService userService;

        @BeforeEach
        void setUp() {
                userService = new UserService(httpClient);
        }

        @Nested
        @DisplayName("me() method")
        class MeMethodTests {

                @Test
                @DisplayName("should retrieve current user information successfully")
                void shouldRetrieveCurrentUserInformationSuccessfully() {
                        // Arrange
                        UserResponse expectedUser = UserResponse.builder()
                                        .name("John Doe")
                                        .email("john.doe@example.com")
                                        .role("ADMIN")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(expectedUser);

                        // Act
                        UserResponse result = userService.me();

                        // Assert
                        assertNotNull(result);
                        assertEquals("John Doe", result.getName());
                        assertEquals("john.doe@example.com", result.getEmail());
                        assertEquals("ADMIN", result.getRole());
                        verify(httpClient, times(1)).get("/users/me", UserResponse.class);
                }

                @Test
                @DisplayName("should call correct endpoint")
                void shouldCallCorrectEndpoint() {
                        // Arrange
                        UserResponse mockResponse = UserResponse.builder()
                                        .name("Test User")
                                        .email("test@example.com")
                                        .role("USER")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(mockResponse);

                        // Act
                        userService.me();

                        // Assert
                        verify(httpClient, times(1)).get("/users/me", UserResponse.class);
                }

                @Test
                @DisplayName("should retrieve user with various roles")
                void shouldRetrieveUserWithVariousRoles() {
                        // Test with different roles
                        String[] roles = { "ADMIN", "USER", "VIEWER", "EDITOR", "GUEST" };

                        for (String role : roles) {
                                // Arrange
                                UserResponse mockResponse = UserResponse.builder()
                                                .name("User with " + role)
                                                .email("user." + role.toLowerCase() + "@example.com")
                                                .role(role)
                                                .build();

                                when(httpClient.get("/users/me", UserResponse.class))
                                                .thenReturn(mockResponse);

                                // Act
                                UserResponse result = userService.me();

                                // Assert
                                assertNotNull(result);
                                assertEquals(role, result.getRole());
                                assertTrue(result.getEmail().contains(role.toLowerCase()));
                        }
                }

                @Test
                @DisplayName("should retrieve user with all fields populated")
                void shouldRetrieveUserWithAllFieldsPopulated() {
                        // Arrange
                        UserResponse expectedUser = UserResponse.builder()
                                        .name("Jane Smith")
                                        .email("jane.smith@ararahq.com")
                                        .role("ADMIN")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(expectedUser);

                        // Act
                        UserResponse result = userService.me();

                        // Assert
                        assertNotNull(result);
                        assertNotNull(result.getName());
                        assertNotNull(result.getEmail());
                        assertNotNull(result.getRole());
                        assertEquals("Jane Smith", result.getName());
                        assertEquals("jane.smith@ararahq.com", result.getEmail());
                        assertEquals("ADMIN", result.getRole());
                }

                @Test
                @DisplayName("should return user response with email verification")
                void shouldReturnUserResponseWithEmailVerification() {
                        // Arrange
                        String expectedEmail = "verified@example.com";
                        UserResponse mockResponse = UserResponse.builder()
                                        .name("Verified User")
                                        .email(expectedEmail)
                                        .role("USER")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(mockResponse);

                        // Act
                        UserResponse result = userService.me();

                        // Assert
                        assertNotNull(result);
                        assertEquals(expectedEmail, result.getEmail());
                        assertTrue(result.getEmail().contains("@example.com"));
                }

                @Test
                @DisplayName("should retrieve multiple times independently")
                void shouldRetrieveMultipleTimesIndependently() {
                        // Arrange
                        UserResponse mockResponse = UserResponse.builder()
                                        .name("Test User")
                                        .email("test@example.com")
                                        .role("USER")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(mockResponse);

                        // Act
                        UserResponse first = userService.me();
                        UserResponse second = userService.me();
                        UserResponse third = userService.me();

                        // Assert
                        assertEquals(first.getName(), second.getName());
                        assertEquals(second.getEmail(), third.getEmail());
                        verify(httpClient, times(3)).get("/users/me", UserResponse.class);
                }

                @Test
                @DisplayName("should handle user with special characters in name")
                void shouldHandleUserWithSpecialCharactersInName() {
                        // Arrange
                        UserResponse mockResponse = UserResponse.builder()
                                        .name("José da Silva")
                                        .email("jose.silva@example.com")
                                        .role("USER")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(mockResponse);

                        // Act
                        UserResponse result = userService.me();

                        // Assert
                        assertNotNull(result);
                        assertEquals("José da Silva", result.getName());
                        assertTrue(result.getName().contains("José"));
                }

                @Test
                @DisplayName("should handle user with minimal information")
                void shouldHandleUserWithMinimalInformation() {
                        // Arrange
                        UserResponse mockResponse = UserResponse.builder()
                                        .name("User")
                                        .email("user@example.com")
                                        .role("GUEST")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(mockResponse);

                        // Act
                        UserResponse result = userService.me();

                        // Assert
                        assertNotNull(result);
                        assertNotNull(result.getName());
                        assertNotNull(result.getEmail());
                        assertNotNull(result.getRole());
                }
        }

        @Nested
        @DisplayName("User authentication context")
        class AuthenticationContextTests {

                @Test
                @DisplayName("should represent authenticated user")
                void shouldRepresentAuthenticatedUser() {
                        // Arrange
                        UserResponse authenticatedUser = UserResponse.builder()
                                        .name("Authenticated User")
                                        .email("auth@example.com")
                                        .role("ADMIN")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(authenticatedUser);

                        // Act
                        UserResponse result = userService.me();

                        // Assert
                        assertNotNull(result);
                        assertTrue(result.getRole().equals("ADMIN") || result.getRole().equals("USER"));
                }

                @Test
                @DisplayName("should always return same endpoint regardless of multiple calls")
                void shouldAlwaysReturnSameEndpointRegardlessOfMultipleCalls() {
                        // Arrange
                        UserResponse mockResponse = UserResponse.builder()
                                        .name("Consistent User")
                                        .email("consistent@example.com")
                                        .role("USER")
                                        .build();

                        when(httpClient.get("/users/me", UserResponse.class))
                                        .thenReturn(mockResponse);

                        // Act & Assert
                        for (int i = 0; i < 5; i++) {
                                UserResponse result = userService.me();
                                assertEquals("consistent@example.com", result.getEmail());
                        }

                        verify(httpClient, times(5)).get("/users/me", UserResponse.class);
                }
        }

        @Nested
        @DisplayName("updateMe() method")
        class UpdateMeMethodTests {

                @Test
                @DisplayName("should update user information successfully")
                void shouldUpdateUserInformationSuccessfully() {
                        // Arrange
                        UpdateUserRequest request = UpdateUserRequest.builder()
                                        .name("New Name")
                                        .phoneNumber("+5511999998888")
                                        .build();

                        UserResponse expectedResponse = UserResponse.builder()
                                        .name("New Name")
                                        .email("john.doe@example.com")
                                        .phoneNumber("+5511999998888")
                                        .build();

                        when(httpClient.patch("/users/me", request, UserResponse.class))
                                        .thenReturn(expectedResponse);

                        // Act
                        UserResponse result = userService.updateMe(request);

                        // Assert
                        assertNotNull(result);
                        assertEquals("New Name", result.getName());
                        assertEquals("+5511999998888", result.getPhoneNumber());
                        verify(httpClient, times(1)).patch("/users/me", request, UserResponse.class);
                }
        }
}
