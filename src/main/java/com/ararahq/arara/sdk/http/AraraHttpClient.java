package com.ararahq.arara.sdk.http;

import com.ararahq.arara.sdk.config.AraraConfig;
import com.ararahq.arara.sdk.exceptions.AraraApiException;
import com.ararahq.arara.sdk.exceptions.AraraAuthException;
import com.ararahq.arara.sdk.exceptions.AraraException;
import com.ararahq.arara.sdk.exceptions.AraraNetworkException;
import com.ararahq.arara.sdk.interceptors.AuthInterceptor;
import com.ararahq.arara.sdk.models.AraraError;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Internal HTTP client based on OkHttp for making calls to the Arara API.
 */
public class AraraHttpClient {
    private static final Logger log = LoggerFactory.getLogger(AraraHttpClient.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public AraraHttpClient(AraraConfig config) {
        this.baseUrl = config.getBaseUrl();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout())
                .readTimeout(config.getReadTimeout())
                .addInterceptor(new AuthInterceptor(config.getApiKey()))
                .build();
    }

    /**
     * Performs a GET request.
     */
    public <T> T get(String path, Class<T> responseType) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .get()
                .build();
        return execute(request, responseType);
    }

    /**
     * Performs a POST request with JSON body.
     */
    public <T> T post(String path, Object body, Class<T> responseType) {
        return requestWithBody("POST", path, body, responseType);
    }

    /**
     * Performs a PATCH request with JSON body.
     */
    public <T> T patch(String path, Object body, Class<T> responseType) {
        return requestWithBody("PATCH", path, body, responseType);
    }

    /**
     * Performs a DELETE request.
     */
    public void delete(String path) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .delete()
                .build();
        execute(request, Void.class);
    }

    private <T> T requestWithBody(String method, String path, Object body, Class<T> responseType) {
        try {
            String json = objectMapper.writeValueAsString(body);
            RequestBody requestBody = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(baseUrl + path)
                    .method(method, requestBody)
                    .build();

            return execute(request, responseType);
        } catch (IOException e) {
            throw new AraraException("Error serializing object to JSON", e);
        }
    }

    private <T> T execute(Request request, Class<T> responseType) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleErrorResponse(response);
            }

            if (responseType == Void.class || response.body() == null) {
                return null;
            }

            return objectMapper.readValue(response.body().string(), responseType);
        } catch (IOException e) {
            log.error("Network error accessing Arara API: {}", request.url(), e);
            throw new AraraNetworkException("Communication failure with Arara API", e);
        }
    }

    private void handleErrorResponse(Response response) throws IOException {
        String body = response.body() != null ? response.body().string() : "";
        AraraError errorDetails = null;

        try {
            if (!body.isEmpty()) {
                errorDetails = objectMapper.readValue(body, AraraError.class);
            }
        } catch (Exception e) {
            log.warn("Could not parse API error: {}", body);
        }

        int code = response.code();
        if (code == 401 || code == 403) {
            throw new AraraAuthException(errorDetails != null ? errorDetails.getMessage() : "Unauthorized");
        }

        throw new AraraApiException(code, errorDetails);
    }
}
