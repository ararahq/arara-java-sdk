package com.ararahq.arara.sdk.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Builds a URL-encoded query string, skipping null values.
 */
public final class QueryString {
    private final StringBuilder builder = new StringBuilder();

    private QueryString() {
    }

    public static QueryString create() {
        return new QueryString();
    }

    /**
     * Appends a parameter when the value is not null.
     */
    public QueryString add(String name, Object value) {
        if (value == null) {
            return this;
        }
        builder.append(builder.length() == 0 ? '?' : '&')
                .append(encode(name))
                .append('=')
                .append(encode(String.valueOf(value)));
        return this;
    }

    /**
     * @return The path with the encoded query string appended.
     */
    public String appendTo(String path) {
        return path + builder;
    }

    /**
     * Encodes a single path segment (e.g. a phone number with '+').
     */
    public static String encodePathSegment(String segment) {
        return encode(segment).replace("+", "%20");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
