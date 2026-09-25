# Arara Java SDK

[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.java.com/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
[![Docs](https://img.shields.io/badge/Docs-docs.ararahq.com-orange)](https://docs.ararahq.com)

SDK oficial em Java para a API de mensagens WhatsApp da **Arara**.

## Requisitos

- Java 17 ou superior
- Chave de API da Arara (painel → Configurações → Chaves de API)

## Instalação

A partir da 2.0.0 o SDK é publicado no **Maven Central**, sem repositório extra nem token.

Gradle:

```gradle
dependencies {
    implementation 'com.ararahq:arara-java-sdk:2.0.0'
}
```

Maven:

```xml
<dependency>
    <groupId>com.ararahq</groupId>
    <artifactId>arara-java-sdk</artifactId>
    <version>2.0.0</version>
</dependency>
```

Cada versão também sai no GitHub Packages (`https://maven.pkg.github.com/ararahq/arara-java-sdk`), que exige token com `read:packages` até para leitura.

## Permissões da chave

A API confere a permissão da chave em cada chamada. Envio, templates, campanhas e números funcionam com as permissões específicas (`MESSAGES_SEND`, `TEMPLATES_WRITE`, `CAMPAIGNS_SEND`, `READ`).

**Exigem chave ADMIN:** `getAuth().me()`, `contacts`, `conversations`, `wallet`, `optOuts` e `organizations` (perfil comercial e plano). Com chave sem ADMIN, essas chamadas lançam `AraraAuthException` (403).

`getMessages().getById(id)` com 403 de corpo vazio significa mensagem de outra conta (ou inexistente para essa chave): o SDK lança `AraraApiException` com `getCode()` = `NOT_FOUND` e status 403, não erro de autenticação.

Gerenciar chaves de API e o webhook da organização não é possível por chave: faça pelo painel.

## Início rápido

```java
Arara arara = Arara.builder()
        .apiKey(System.getenv("ARARA_API_KEY"))
        .build();

MessageResponse sent = arara.getMessages().send(SendMessageRequest.builder()
        .receiver("+5511999998888")
        .templateName("boas_vindas")
        .templateVariables(List.of("Ana"))
        .build());
```

Uma chave de idempotência em branco é recusada com `AraraException`; espaços nas pontas são removidos.

O `receiver` aceita `whatsapp:+5511999998888`, `+5511999998888` ou só os dígitos (7 a 15 dígitos, mesma regra da API).

### Idempotência e retry

`getMessages().send`, `getMessages().sendBatch` e `getCampaigns().create` **sempre** mandam o header `Idempotency-Key`. Sem chave sua, o SDK gera um UUID v4 por chamada e reutiliza a mesma chave em todos os retries dela, então um timeout nunca vira envio duplicado. Para retentar por conta própria (por exemplo, depois de reiniciar o processo), passe a sua chave:

```java
arara.getMessages().send(request, "pedido-4521-lembrete");
arara.getCampaigns().create(campaign, "campanha-black-friday");
```

O retry automático (padrão 3, `maxRetries`) cobre falha de rede, 5xx e 429, e honra `Retry-After` até 30 s; acima disso não espera e lança a exceção com `getRetryAfter()`. POST e PATCH sem `Idempotency-Key` **nunca** são repetidos; GET, PUT e DELETE sim.

## Recursos

| Serviço | Métodos | Endpoint |
|---|---|---|
| `getMessages()` | `send`, `sendBatch` (até 1000), `getById`, `listByBatch` | `/v1/messages` |
| `getCampaigns()` | `create`, `list`, `getById`, `cancel` | `/v1/campaigns` |
| `getTemplates()` | `create`, `list`, `getById`, `getStatus`, `delete`, `analytics` | `/v1/templates` |
| `getSmartLinks()` | `create`, `update`, `list`, `stats` | `/v1/smart-links/whatsapp` |
| `getNumbers()` | `list`, `update`, `delete`, `request`, `listRequests`, `sync`, `warming` | `/v1/organizations/me/numbers` |
| `getAuth()` | `me` (ADMIN) | `/auth/me` |
| `getContacts()` | `list`, `importBatch`, `stats`, `reactivationCandidates`, `listTags`, `get`, `update`, `messages` (ADMIN) | `/v1/contacts` |
| `getConversations()` | `list`, `leadStats`, `messages`, `reply`, `updateStatus`, `windowStatus` (ADMIN) | `/v1/conversations` |
| `getWallet()` | `transactions`, `getAutoRecharge`, `updateAutoRecharge` (ADMIN) | `/v1/wallet` |
| `getOptOuts()` | `list`, `add`, `get`, `remove` (ADMIN) | `/v1/opt-outs` |
| `getOrganizations()` | `me`, `updateBusinessProfile`, `getPlan`, `changePlan` (ADMIN) | `/v1/organizations/me` |

### Templates são por id

`getById`, `getStatus`, `delete` e `analytics(id, period)` recebem o **id (UUID)** do template, nunca o nome. Para achar pelo nome, filtre a lista:

```java
PaginatedResponse<TemplateResponse> page = arara.getTemplates().list("boas_vindas", "APPROVED", 0, 50);
UUID id = page.getData().get(0).getId();
```

### Paginação

A API não usa um formato só, e o SDK segue cada endpoint:

- `getTemplates().list` e `getSmartLinks().list` → `PaginatedResponse<T>` com `getData()` e `getPagination()` (`page`, `size`, `totalElements`, `totalPages`).
- `getCampaigns().list` → `CampaignPage` e `getWallet().transactions` → `WalletTransactionPage`, ambos com `getContent()`, `getTotalPages()`, `getTotalElements()`.
- `getContacts().list` → `ContactsListResponse` (`contacts`, `total`, `page`, `size`, `totalPages`).

## Erros

Toda resposta de erro vira exceção; nada é engolido.

```java
try {
    arara.getMessages().send(request);
} catch (PlanFeatureLockedException e) {
    // 403 PLAN_FEATURE_LOCKED
    log.info("Libere {} no plano {} (atual: {})", e.getFeature(), e.getUpgradeTo(), e.getCurrentPlan());
} catch (AraraAuthException e) {
    // 401, ou 403 sem código: chave inválida, expirada ou sem permissão
} catch (AraraRateLimitException e) {
    // 429 depois dos retries; e.getRetryAfter()
} catch (AraraApiException e) {
    // e.getStatusCode(), e.getCode() (ex.: INVALID_RECIPIENT), e.getMessage(), e.getDetails(), e.getRetryAfter()
} catch (AraraNetworkException e) {
    // timeout ou falha de conexão
}
```

Um 403 com código de negócio (`NO_DEDICATED_NUMBER`, `PLAN_LIMIT_REACHED`, `RESOURCE_FORBIDDEN`...) é `AraraApiException` com o código, não erro de autenticação.

## Configuração

```java
Arara arara = Arara.builder()
        .apiKey(apiKey)
        .baseUrl("https://api.ararahq.com")
        .connectTimeout(Duration.ofSeconds(10))
        .readTimeout(Duration.ofSeconds(30))
        .callTimeout(Duration.ofMinutes(2))
        .maxRetries(3)
        .build();
```

## Desenvolvimento

```bash
./gradlew build          # compila, roda os testes (contra servidor HTTP fake) e confere cobertura mínima de 80%
./gradlew jacocoTestReport
```

A publicação é automática: todo push na `main` com versão nova em `gradle.properties` publica no Maven Central (assinado) e no GitHub Packages, e cria a tag `vX.Y.Z` com release.

## Licença

[MIT](LICENSE). Dúvidas: suporte@ararahq.com.
