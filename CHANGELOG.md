# Changelog

## 2.0.0 (2026-09-24)

Alinha o SDK com a API real por chave de API. Tem mudanças incompatíveis.

### Incompatível
- Removido `getUsers()` (`/users/me` recusa chave de API). Use `getAuth().me()` → `GET /auth/me` (chave ADMIN). `UserResponse` agora tem `name`, `email`, `role`, `emailPending`.
- Removido `getApiKeys()`: a API recusa gerenciar chaves por chave (403 `RESOURCE_FORBIDDEN`).
- `getTemplates().list()` devolve `PaginatedResponse<TemplateResponse>` (`data` + `pagination`) em vez de `List`; antes quebrava sempre na desserialização.
- `getSmartLinks().list()` devolve `PaginatedResponse<WhatsAppSmartLinkResponse>`.
- 403 com código de negócio deixou de ser `AraraAuthException`: vira `AraraApiException` com `getCode()`, e `PLAN_FEATURE_LOCKED` vira `PlanFeatureLockedException` (`getFeature`, `getCurrentPlan`, `getUpgradeTo`). `AraraAuthException` agora estende `AraraApiException` e só cobre 401 e 403 sem código.
- POST e PATCH sem `Idempotency-Key` não são mais repetidos pelo retry automático.

### Corrigido
- `getMessages().send` e `getCampaigns().create` sempre mandam `Idempotency-Key` (UUID v4 gerado ou o seu), reutilizado nos retries: timeout ou 5xx não vira envio duplicado e cobrado.
- `receiver` aceita `whatsapp:+55...`, `+55...` e só dígitos, com a regra da API (7 a 15 dígitos).
- Filtros de contatos e conversas e telefones no path são codificados na URL (`&`, `#`, `+` não corrompem mais a query).
- Erro do filtro de chave no formato padrão do Spring (`{timestamp,status,error,path}`) é lido como falha de autenticação, não como código de negócio.
- `getMessages().getById` com 403 de corpo vazio vira `AraraApiException` `NOT_FOUND` (mensagem de outra conta), não erro de chave.
- `Retry-After` acima de 30 s não segura a thread: a exceção sai com `getRetryAfter()`.
- Chave de idempotência em branco e api key em branco são recusadas; item nulo em lote ou campanha falha com o índice.
- `AraraApiException` expõe `getCode()`, `getDetails()` e `getRetryAfter()` (também em 503).

### Novo
- `getMessages().sendBatch` (`POST /v1/messages/batch`, até 1000) e `getMessages().listByBatch`.
- `getCampaigns().list` (`CampaignPage`) e `getCampaigns().cancel`; `CampaignRequest.scheduledAt`.
- `getTemplates().list(name, status, page, size)` e `getTemplates().analytics(...)`; `CreateTemplateRequest.headerType`.
- `getOptOuts()` (`/v1/opt-outs`).
- `MessageResponse.reason`; `CampaignResponse.scheduledAt`; `CampaignListItem.scheduledAt/createdAt`.
- `getCampaigns().getById` devolve `CampaignDetail` (contadores de entrega, leitura, clique, conversão, bloqueio e estorno, `startedAt`, `finishedAt`).
- Publicação no Maven Central (assinada), além do GitHub Packages.

## 1.8.1 (2026-07-14)

- Última versão publicada só no GitHub Packages.
