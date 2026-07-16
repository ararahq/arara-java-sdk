package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * Paginated wallet transaction history.
 */
@Value
@Builder
@Jacksonized
public class WalletTransactionPage {
    List<WalletTransaction> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
}
