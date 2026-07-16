package com.ararahq.arara.sdk.services;

import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.models.AutoRechargeSettings;
import com.ararahq.arara.sdk.models.UpdateAutoRechargeRequest;
import com.ararahq.arara.sdk.models.WalletTransactionPage;
import com.ararahq.arara.sdk.utils.ValidationUtils;

/**
 * Service for wallet balance transactions and auto-recharge settings.
 */
public class WalletService {
    private final AraraHttpClient httpClient;

    public WalletService(AraraHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Lists wallet transactions. GET /v1/wallet/transactions
     */
    public WalletTransactionPage transactions(int page, int size) {
        return httpClient.get(
                "/v1/wallet/transactions?page=" + page + "&size=" + size, WalletTransactionPage.class);
    }

    /**
     * Returns auto-recharge settings. GET /v1/wallet/auto-recharge
     */
    public AutoRechargeSettings getAutoRecharge() {
        return httpClient.get("/v1/wallet/auto-recharge", AutoRechargeSettings.class);
    }

    /**
     * Updates auto-recharge settings. PATCH /v1/wallet/auto-recharge
     */
    public AutoRechargeSettings updateAutoRecharge(UpdateAutoRechargeRequest request) {
        ValidationUtils.checkNotNull(request, "request");
        return httpClient.patch("/v1/wallet/auto-recharge", request, AutoRechargeSettings.class);
    }
}
