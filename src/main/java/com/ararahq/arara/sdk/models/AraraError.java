package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

/**
 * Internal model to map errors returned by the API.
 * The API responds with a nested envelope: {"error": {"code": "...", "message": "...", "details": {}}}.
 */
@Value
@Builder
@Jacksonized
public class AraraError {
    String code;
    String message;
    Map<String, Object> details;
}
