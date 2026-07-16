package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * Number cards plus plan slot info.
 */
@Value
@Builder
@Jacksonized
public class NumbersResponse {
    List<NumberCard> numbers;
    NumbersSlot slot;
}
