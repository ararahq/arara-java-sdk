package com.ararahq.arara.sdk.http;

import com.ararahq.arara.sdk.config.AraraConfig;
import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.exceptions.AraraAuthException;
import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.exceptions.AraraNetworkException;
import com.ararahq.arara.sdk.exceptions.AraraRateLimitException;
import com.ararahq.arara.sdk.exceptions.PlanFeatureLockedException;
import com.ararahq.arara.sdk.interceptors.AuthInterceptor;
import com.ararahq.arara.sdk.interceptors.RetryInterceptor;
import com.ararahq.arara.sdk.models.AraraError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Internal HTTP client based on OkHttp for making calls to the Arara API.
 */
public class AraraHttpClient {
    private static final Logger log = LoggerFactory.getLogger(AraraHttpClient.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_FORBIDDEN = 403;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final String RETRY_AFTER_HEADER = "Retry-After";
    private static final TypeReference<Map<String, Object>> DETAILS_TYPE =
            new TypeReference<Map<String, Object>>() {
            };

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public AraraHttpClient(AraraConfig config) {
        this.baseUrl = config.getBaseUrl();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout())
                .readTimeout(config.getReadTimeout())
                .writeTimeout(config.getWriteTimeout())
                .callTimeout(config.getCallTimeout())
                .addInterceptor(new AuthInterceptor(config.getApiKey()))
                .addInterceptor(new RetryInterceptor(config.getMaxRetries()))
                .build();
    }

    /**
     * Performs a GET request.
     */
    public <T> T get(String path, Class<T> responseType) {
        return execute(buildGet(path), responseType);
    }

    /**
     * Performs a GET request with a generic response type.
     */
    public <T> T get(String path, TypeReference<T> responseType) {
        return execute(buildGet(path), responseType);
    }

    /**
     * Performs a POST request with JSON body.
     */
    public <T> T post(String path, Object body, Class<T> responseType) {
        return execute(buildBody("POST", path, body, Collections.emptyMap()), responseType);
    }

    /**
     * Performs a POST request with JSON body and extra headers (e.g. Idempotency-Key).
     */
    public <T> T post(String path, Object body, Map<String, String> headers, Class<T> responseType) {
        return execute(buildBody("POST", path, body, headers), responseType);
    }

    /**
     * Performs a POST request with a generic response type.
     */
    public <T> T post(String path, Object body, TypeReference<T> responseType) {
        return execute(buildBody("POST", path, body, Collections.emptyMap()), responseType);
    }

    /**
     * Performs a PUT request with JSON body.
     */
    public <T> T put(String path, Object body, Class<T> responseType) {
        return execute(buildBody("PUT", path, body, Collections.emptyMap()), responseType);
    }

    /**
     * Performs a PUT request with a generic response type.
     */
    public <T> T put(String path, Object body, TypeReference<T> responseType) {
        return execute(buildBody("PUT", path, body, Collections.emptyMap()), responseType);
    }

    /**
     * Performs a PATCH request with JSON body.
     */
    public <T> T patch(String path, Object body, Class<T> responseType) {
        return execute(buildBody("PATCH", path, body, Collections.emptyMap()), responseType);
    }

    /**
     * Performs a PATCH request with a generic response type.
     */
    public <T> T patch(String path, Object body, TypeReference<T> responseType) {
        return execute(buildBody("PATCH", path, body, Collections.emptyMap()), responseType);
    }

    /**
     * Performs a DELETE request, discarding the response body.
     */
    public void delete(String path) {
        execute(buildDelete(path), Void.class);
    }

    /**
     * Performs a DELETE request with a generic response type.
     */
    public <T> T delete(String path, TypeReference<T> responseType) {
        return execute(buildDelete(path), responseType);
    }

    private String url(String path) {
        boolean baseEndsWithSlash = baseUrl.endsWith("/");
        boolean pathStartsWithSlash = path.startsWith("/");
        if (baseEndsWithSlash && pathStartsWithSlash) {
            return baseUrl + path.substring(1);
        }
        if (!baseEndsWithSlash && !pathStartsWithSlash) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private Request buildGet(String path) {
        return new Request.Builder().url(url(path)).get().build();
    }

    private Request buildDelete(String path) {
        return new Request.Builder().url(url(path)).delete().build();
    }

    private Request buildBody(String method, String path, Object body, Map<String, String> headers) {
        try {
            RequestBody requestBody = body == null
                    ? RequestBody.create("", JSON)
                    : RequestBody.create(objectMapper.writeValueAsString(body), JSON);
            Request.Builder builder = new Request.Builder()
                    .url(url(path))
                    .method(method, requestBody);
            headers.forEach(builder::header);
            return builder.build();
        } catch (IOException e) {
            throw new AraraException("Error serializing object to JSON", e);
        }
    }

    private <T> T execute(Request request, Class<T> responseType) {
        if (responseType == Void.class) {
            call(request, true);
            return null;
        }
        String body = call(request, false);
        if (body == null || body.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException e) {
            throw new AraraNetworkException("Error deserializing Arara API response", e);
        }
    }

    private <T> T execute(Request request, TypeReference<T> responseType) {
        String body = call(request, false);
        if (body == null || body.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException e) {
            throw new AraraNetworkException("Error deserializing Arara API response", e);
        }
    }

    private String call(Request request, boolean discardBody) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw toException(response);
            }
            if (discardBody || response.body() == null) {
                return null;
            }
            return response.body().string();
        } catch (IOException e) {
            log.error("Network error accessing Arara API. [url={}, reason={}]", request.url(), e.getMessage());
            throw new AraraNetworkException("Communication failure with Arara API", e);
        }
    }

    private AraraApiException toException(Response response) throws IOException {
        String body = response.body() != null ? response.body().string() : "";
        AraraError errorDetails = parseError(body);
        String code = errorDetails != null ? errorDetails.getCode() : null;
        int status = response.code();

        if (status == HTTP_UNAUTHORIZED || (status == HTTP_FORBIDDEN && code == null)) {
            return new AraraAuthException(status, errorDetails);
        }
        if (status == HTTP_FORBIDDEN && PlanFeatureLockedException.CODE.equals(code)) {
            return new PlanFeatureLockedException(errorDetails);
        }
        if (status == HTTP_TOO_MANY_REQUESTS) {
            return new AraraRateLimitException(errorDetails,
                    RetryInterceptor.parseRetryAfter(response.header(RETRY_AFTER_HEADER)));
        }
        return new AraraApiException(status, errorDetails,
                RetryInterceptor.parseRetryAfter(response.header(RETRY_AFTER_HEADER)));
    }

    private AraraError parseError(String body) {
        if (body == null || body.isEmpty()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode error = root.path("error");
            if (error.isObject()) {
                return AraraError.builder()
                        .code(textOrNull(error.path("code")))
                        .message(textOrNull(error.path("message")))
                        .details(error.path("details").isObject()
                                ? objectMapper.convertValue(error.path("details"), DETAILS_TYPE)
                                : null)
                        .build();
            }
            if (isSpringDefaultError(root)) {
                String message = textOrNull(root.path("message"));
                return AraraError.builder()
                        .message(message != null ? message : textOrNull(error))
                        .build();
            }
            return AraraError.builder()
                    .code(textOrNull(error))
                    .message(textOrNull(root.path("message")))
                    .build();
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Could not parse API error body. [reason={}]", e.getMessage());
            return null;
        }
    }

    private static boolean isSpringDefaultError(JsonNode root) {
        return root.has("timestamp") || root.has("path");
    }

    private static String textOrNull(JsonNode node) {
        if (node == null || !node.isTextual() || node.asText().isBlank()) {
            return null;
        }
        return node.asText();
    }
}
