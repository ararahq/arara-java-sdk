package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.AutoRechargeSettings;
import com.ararahq.arara.sdk.models.UpdateAutoRechargeRequest;
import com.ararahq.arara.sdk.models.WalletTransactionPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WalletService Tests")
class WalletServiceTest {

    @Mock
    private AraraHttpClient httpClient;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService(httpClient);
    }

    @Test
    @DisplayName("should list wallet transactions with pagination")
    void shouldListTransactions() {
        WalletTransactionPage expected = WalletTransactionPage.builder()
                .page(0)
                .size(20)
                .totalElements(3)
                .build();
        when(httpClient.get("/v1/wallet/transactions?page=0&size=20", WalletTransactionPage.class))
                .thenReturn(expected);

        WalletTransactionPage result = walletService.transactions(0, 20);

        assertEquals(3, result.getTotalElements());
        verify(httpClient, times(1))
                .get("/v1/wallet/transactions?page=0&size=20", WalletTransactionPage.class);
    }

    @Test
    @DisplayName("should return auto-recharge settings")
    void shouldGetAutoRecharge() {
        AutoRechargeSettings expected = AutoRechargeSettings.builder()
                .enabled(true)
                .threshold(BigDecimal.valueOf(10))
                .amount(BigDecimal.valueOf(50))
                .build();
        when(httpClient.get("/v1/wallet/auto-recharge", AutoRechargeSettings.class)).thenReturn(expected);

        AutoRechargeSettings result = walletService.getAutoRecharge();

        assertTrue(result.isEnabled());
        assertEquals(BigDecimal.valueOf(50), result.getAmount());
        verify(httpClient, times(1)).get("/v1/wallet/auto-recharge", AutoRechargeSettings.class);
    }

    @Test
    @DisplayName("should update auto-recharge settings")
    void shouldUpdateAutoRecharge() {
        UpdateAutoRechargeRequest request = UpdateAutoRechargeRequest.builder()
                .enabled(true)
                .threshold(BigDecimal.valueOf(15))
                .amount(BigDecimal.valueOf(100))
                .build();
        AutoRechargeSettings expected = AutoRechargeSettings.builder()
                .enabled(true)
                .amount(BigDecimal.valueOf(100))
                .build();
        when(httpClient.patch("/v1/wallet/auto-recharge", request, AutoRechargeSettings.class))
                .thenReturn(expected);

        AutoRechargeSettings result = walletService.updateAutoRecharge(request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        verify(httpClient, times(1))
                .patch("/v1/wallet/auto-recharge", request, AutoRechargeSettings.class);
    }

    @Test
    @DisplayName("should reject null auto-recharge update request")
    void shouldRejectNullUpdate() {
        assertThrows(AraraException.class, () -> walletService.updateAutoRecharge(null));
        verifyNoInteractions(httpClient);
    }
}
