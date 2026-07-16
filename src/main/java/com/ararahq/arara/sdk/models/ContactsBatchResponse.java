package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;
import java.util.UUID;

/**
 * Summary of a contact batch import.
 */
@Value
@Builder
@Jacksonized
public class ContactsBatchResponse {
    UUID importId;
    int created;
    int updated;
    int skipped;
    List<ContactsBatchError> errors;
}
