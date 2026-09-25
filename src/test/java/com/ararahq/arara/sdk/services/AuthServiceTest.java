package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraAuthException;
import com.ararahq.arara.sdk.models.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AuthService against fake API")
class AuthServiceTest extends FakeApi {

    @Test
    @DisplayName("should GET /auth/me without the /v1 prefix and parse the user")
    void shouldGetMe() throws Exception {
        respond(200, "{\"name\":\"Ana\",\"email\":\"ana@loja.com\",\"role\":\"ADMIN\",\"emailPending\":false}");

        UserResponse me = arara.getAuth().me();

        take("GET", "/auth/me");
        assertEquals("ana@loja.com", me.getEmail());
        assertEquals("ADMIN", me.getRole());
    }

    @Test
    @DisplayName("should raise auth error when the key is not ADMIN (403 without envelope)")
    void shouldRaiseAuthErrorForNonAdminKey() {
        respond(403, "{\"timestamp\":\"2026-09-24T00:00:00Z\",\"status\":403,\"error\":\"Forbidden\","
                + "\"message\":\"API Key permission insufficient\",\"path\":\"/auth/me\"}");

        AraraAuthException error = assertThrows(AraraAuthException.class, () -> arara.getAuth().me());

        assertEquals(403, error.getStatusCode());
        assertEquals("API Key permission insufficient", error.getMessage());
    }
}
