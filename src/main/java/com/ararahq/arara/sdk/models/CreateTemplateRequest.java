package com.ararahq.arara.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

/**
 * Payload for creating a new WhatsApp template.
 */
@Value
@Builder
@Jacksonized
public class CreateTemplateRequest {
    String name;
    String category; // MARKETING, UTILITY, AUTHENTICATION
    String body;
    @Builder.Default
    String language = "pt_BR";

    String header;
    String footer;
    List<TemplateButton> buttons;
    Map<String, String> samples;
    List<String> variableExamples;

    @Value
    @Builder
    @Jacksonized
    public static class TemplateButton {
        ButtonType type;
        String text;
        String url;
        String phone;
        Map<String, Object> extraConfig;
    }

    public enum ButtonType {
        QUICK_REPLY, PHONE_NUMBER, URL, SMART_LINK, COPY_CODE
    }
}
