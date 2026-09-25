package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.models.UpdateAutoRechargeRequest;
import com.ararahq.arara.sdk.models.WalletTransactionPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("WalletService against fake API")
class WalletServiceTest extends FakeApi {

    @Test
    @DisplayName("should parse wallet transactions as {content,totalPages,totalElements}")
    void shouldListTransactions() throws Exception {
        respond(200, "{\"content\":[{\"id\":\"t1\",\"amount\":50.00,\"type\":\"CREDIT\"}],"
                + "\"totalPages\":1,\"totalElements\":1}");

        WalletTransactionPage page = arara.getWallet().transactions(0, 20);

        take("GET", "/v1/wallet/transactions?page=0&size=20");
        assertEquals("t1", page.getContent().get(0).getId());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    @DisplayName("should read and patch auto-recharge")
    void shouldCoverAutoRecharge() throws Exception {
        respond(200, "{\"enabled\":true,\"threshold\":100,\"amount\":500}");
        respond(200, "{\"enabled\":false}");

        assertTrue(arara.getWallet().getAutoRecharge().isEnabled());
        arara.getWallet().updateAutoRecharge(UpdateAutoRechargeRequest.builder()
                .enabled(false).threshold(new BigDecimal("100")).build());

        take("GET", "/v1/wallet/auto-recharge");
        assertEquals(false, json(take("PATCH", "/v1/wallet/auto-recharge")).get("enabled").asBoolean());
    }
}
