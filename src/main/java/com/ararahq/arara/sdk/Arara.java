package com.ararahq.arara.sdk;

import com.ararahq.arara.sdk.config.AraraConfig;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.services.CampaignService;
import com.ararahq.arara.sdk.services.MessageService;
import com.ararahq.arara.sdk.services.TemplateService;
import com.ararahq.arara.sdk.services.UserService;
import lombok.Getter;

/**
 * Main entry point for the Arara Java SDK.
 * Use the Builder to configure and instantiate the client.
 */
@Getter
public class Arara {
    private final MessageService messages;
    private final UserService users;
    private final CampaignService campaigns;
    private final TemplateService templates;

    private Arara(AraraConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            throw new com.ararahq.arara.sdk.exceptions.AraraException(
                    "API Key is required to initialize the SDK.");
        }
        AraraHttpClient httpClient = new AraraHttpClient(config);
        this.messages = new MessageService(httpClient);
        this.users = new UserService(httpClient);
        this.campaigns = new CampaignService(httpClient);
        this.templates = new TemplateService(httpClient);
    }

    /**
     * Starts the builder for SDK configuration.
     */
    public static AraraBuilder builder() {
        return new AraraBuilder();
    }

    /**
     * Builder for the Arara class.
     */
    public static class AraraBuilder {
        private final AraraConfig.AraraConfigBuilder configBuilder = AraraConfig.builder();

        public AraraBuilder apiKey(String apiKey) {
            configBuilder.apiKey(apiKey);
            return this;
        }

        public AraraBuilder baseUrl(String baseUrl) {
            configBuilder.baseUrl(baseUrl);
            return this;
        }

        /**
         * Builds and initializes the Arara client.
         */
        public Arara build() {
            return new Arara(configBuilder.build());
        }
    }
}
