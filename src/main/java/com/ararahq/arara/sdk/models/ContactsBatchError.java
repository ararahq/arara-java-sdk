package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * A single rejected row from a contact batch import.
 */
@Value
@Builder
@Jacksonized
public class ContactsBatchError {
    int index;
    String phone;
    String reason;
}
