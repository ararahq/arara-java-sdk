package com.ararahq.arara.sdk;

import com.ararahq.arara.sdk.config.AraraConfig;
import com.ararahq.arara.sdk.http.AraraHttpClient;
import com.ararahq.arara.sdk.services.ApiKeyService;
import com.ararahq.arara.sdk.services.CampaignService;
import com.ararahq.arara.sdk.services.ContactService;
import com.ararahq.arara.sdk.services.ConversationService;
import com.ararahq.arara.sdk.services.MessageService;
import com.ararahq.arara.sdk.services.NumberService;
import com.ararahq.arara.sdk.services.OrganizationService;
import com.ararahq.arara.sdk.services.SmartLinkService;
import com.ararahq.arara.sdk.services.TemplateService;
import com.ararahq.arara.sdk.services.UserService;
import com.ararahq.arara.sdk.services.WalletService;
import lombok.Getter;

import java.time.Duration;

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
    private final ContactService contacts;
    private final ConversationService conversations;
    private final WalletService wallet;
    private final NumberService numbers;
    private final SmartLinkService smartLinks;
    private final ApiKeyService apiKeys;
    private final OrganizationService organizations;

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
        this.contacts = new ContactService(httpClient);
        this.conversations = new ConversationService(httpClient);
        this.wallet = new WalletService(httpClient);
        this.numbers = new NumberService(httpClient);
        this.smartLinks = new SmartLinkService(httpClient);
        this.apiKeys = new ApiKeyService(httpClient);
        this.organizations = new OrganizationService(httpClient);
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

        public AraraBuilder connectTimeout(Duration connectTimeout) {
            configBuilder.connectTimeout(connectTimeout);
            return this;
        }

        public AraraBuilder readTimeout(Duration readTimeout) {
            configBuilder.readTimeout(readTimeout);
            return this;
        }

        public AraraBuilder writeTimeout(Duration writeTimeout) {
            configBuilder.writeTimeout(writeTimeout);
            return this;
        }

        public AraraBuilder callTimeout(Duration callTimeout) {
            configBuilder.callTimeout(callTimeout);
            return this;
        }

        public AraraBuilder maxRetries(int maxRetries) {
            configBuilder.maxRetries(maxRetries);
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
