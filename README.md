# Arara Java SDK

[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.java.com/)
[![Gradle](https://img.shields.io/badge/Gradle-9.0-blue)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

SDK oficial em Java para integração com a **Plataforma WhatsApp Business Arara**.

## Sobre

O Arara Java SDK é uma biblioteca leve e type-safe para integração perfeita com os serviços de mensagens WhatsApp, gerenciamento de campanhas e usuários da Arara. Construa aplicações robustas de mensagens em Java com o mínimo de esforço.

### Funcionalidades

- 📱 **Serviço de Mensagens** - Envie mensagens WhatsApp (templates e texto livre)
- 📊 **Serviço de Campanhas** - Crie e gerencie campanhas de mensagens em massa
- 👤 **Serviço de Usuários** - Acesse e atualize informações do usuário autenticado
- 📝 **Serviço de Templates** - Crie, gerencie e consulte o status de templates WhatsApp
- ✅ **Validação Robusta** - Validação de requisições com mensagens de erro claras
- 🔐 **Autenticação Segura** - Gerenciamento automático de chave API via interceptadores
- 📦 **Type-Safe** - Segurança de tipos completa com padrão builder
- 🧪 **Bem Testado** - 66 testes unitários com 80% de cobertura
- 📖 **Documentação Completa** - Javadoc abrangente para todas as APIs públicas

## Requisitos

- **Java**: 17 ou superior
- **Gradle**: 7.0 ou superior (ou use o wrapper incluído)
- **Chave de API**: Chave de API válida da Arara do seu painel de controle

## Instalação

### Usando Gradle

Adicione a dependência ao seu `build.gradle`:

```gradle
dependencies {
    implementation 'com.ararahq:arara-java-sdk:0.0.1-SNAPSHOT'
}
```

### Usando Maven

Adicione ao seu `pom.xml`:

```xml
<dependency>
    <groupId>com.ararahq</groupId>
    <artifactId>arara-java-sdk</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Início Rápido

### 1. Inicializar o SDK

```java
import com.ararahq.arara.sdk.Arara;

// Criar o cliente
Arara arara = Arara.builder()
    .apiKey("sua-chave-api-aqui")
    .baseUrl("https://api.ararahq.com/api")
    .build();
```

### 2. Enviar uma Mensagem

```java
import com.ararahq.arara.sdk.models.SendMessageRequest;
import com.ararahq.arara.sdk.models.MessageResponse;

// Enviar uma mensagem de texto livre
SendMessageRequest request = SendMessageRequest.builder()
    .receiver("whatsapp:+5511999998888")
    .body("Olá! Esta é uma mensagem de teste.")
    .build();

MessageResponse response = arara.getMessages().send(request);
System.out.println("ID da Mensagem: " + response.getId());
System.out.println("Status: " + response.getStatus());
```

### 3. Enviar uma Mensagem com Template

```java
SendMessageRequest request = SendMessageRequest.builder()
    .receiver("whatsapp:+5511999998888")
    .templateName("hello_world")
    .templateVariables(Arrays.asList("João", "Silva"))
    .build();

MessageResponse response = arara.getMessages().send(request);
```

### 4. Criar uma Campanha

```java
import com.ararahq.arara.sdk.models.*;
import java.util.Arrays;

List<CampaignContactRequest> contacts = Arrays.asList(
    CampaignContactRequest.builder()
        .to("whatsapp:+5511999998888")
        .variables(Arrays.asList("João"))
        .build(),
    CampaignContactRequest.builder()
        .to("whatsapp:+5511988887777")
        .variables(Arrays.asList("Maria"))
        .build()
);

CampaignRequest campaign = CampaignRequest.builder()
    .name("Campanha de Boas-vindas")
    .templateName("template_boas_vindas")
    .sender("whatsapp:+551140001000")
    .contacts(contacts)
    .build();

CampaignResponse response = arara.getCampaigns().create(campaign);
System.out.println("ID da Campanha: " + response.getId());
System.out.println("Total de Mensagens: " + response.getTotalMessages());
```

### 5. Obter Informações do Usuário

```java
UserResponse user = arara.getUsers().me();
System.out.println("Usuário: " + user.getName());
System.out.println("Email: " + user.getEmail());
System.out.println("Papel: " + user.getRole());

// Atualizar informações do meu perfil
UpdateUserRequest updateRequest = UpdateUserRequest.builder()
    .name("Novo Nome")
    .phoneNumber("+5511999998888")
    .build();
UserResponse updatedUser = arara.getUsers().updateMe(updateRequest);
```

### 6. Gerenciar Templates

```java
import com.ararahq.arara.sdk.models.CreateTemplateRequest;
import com.ararahq.arara.sdk.models.TemplateResponse;
import com.ararahq.arara.sdk.models.TemplateStatusResponse;

// Criar um novo template
CreateTemplateRequest newTemplate = CreateTemplateRequest.builder()
    .name("boas_vindas_v2")
    .category("MARKETING")
    .body("Olá {{1}}, bem-vindo à nossa plataforma!")
    .build();

TemplateResponse template = arara.getTemplates().create(newTemplate);
System.out.println("ID: " + template.getId());

// Consultar status de aprovação na Meta
TemplateStatusResponse status = arara.getTemplates().getStatus(template.getId());
System.out.println("Status: " + status.getStatus());
```

## Estrutura do Projeto

```
src/
├── main/java/com/ararahq/arara/sdk/
│   ├── Arara.java                  # Ponto de entrada principal do SDK
│   ├── config/                     # Classes de configuração
│   ├── exceptions/                 # Tipos de exceção personalizados
│   ├── http/                       # Implementação do cliente HTTP
│   ├── interceptors/               # Interceptadores de requisição/resposta
│   ├── models/                     # Modelos de requisição/resposta
│   ├── services/                   # Serviços de lógica de negócio
│   │   ├── MessageService.java
│   │   ├── CampaignService.java
│   │   ├── UserService.java
│   │   └── TemplateService.java
│   └── utils/                      # Funções utilitárias
└── test/java/com/ararahq/arara/sdk/
    ├── services/                   # Testes unitários dos serviços (38 testes)
    └── utils/                      # Testes unitários de utilitários (28 testes)

build.gradle                         # Configuração do Gradle
```

## Executando Testes

### Rodar todos os testes

```bash
./gradlew test
```

### Gerar relatório de cobertura de testes

```bash
./gradlew jacocoTestReport
```

O relatório de cobertura estará disponível em `build/reports/jacoco/test/html/index.html`

### Rodar uma classe de teste específica

```bash
./gradlew test --tests MessageServiceTest
```

## Referência da API

### MessageService

- `send(SendMessageRequest)` - Enviar uma mensagem (template ou texto livre)
- `getById(String id)` - Recuperar detalhes da mensagem por ID

### CampaignService

- `create(CampaignRequest)` - Criar e iniciar uma nova campanha
- `getById(UUID id)` - Recuperar detalhes da campanha por ID

### UserService

- `me()` - Obter informações do usuário autenticado atual
- `updateMe(UpdateUserRequest)` - Atualizar informações do perfil do usuário

### TemplateService

- `create(CreateTemplateRequest)` - Criar e submeter um novo template para o Meta
- `list()` - Listar todos os templates da conta
- `getById(UUID id)` - Recuperar detalhes de um template por ID
- `delete(UUID id)` - Remover um template
- `getStatus(UUID id)` - Consultar o status de aprovação atual do template

## Tratamento de Erros

O SDK fornece tipos de exceção específicos para melhor tratamento de erros:

```java
import com.ararahq.arara.sdk.exceptions.*;

try {
    MessageResponse response = arara.getMessages().send(request);
} catch (AraraAuthException e) {
    // Tratar erros de autenticação (401, 403)
    System.err.println("Autenticação falhou: " + e.getMessage());
} catch (AraraApiException e) {
    // Tratar erros da API com código de status e detalhes
    System.err.println("Erro da API " + e.getStatusCode() + ": " + e.getMessage());
} catch (AraraNetworkException e) {
    // Tratar erros de rede/timeout
    System.err.println("Erro de rede: " + e.getMessage());
} catch (AraraException e) {
    // Tratar erros gerais do SDK
    System.err.println("Erro do SDK: " + e.getMessage());
}
```

## Contribuindo

Agradecemos o interesse em contribuir para o Arara Java SDK! Por favor, siga estas diretrizes:

### Fluxo de Trabalho

1. **Fork** este repositório
2. Crie uma **branch de feature**: `git checkout -b feat/sua-feature`
3. Faça suas alterações e **commit**:
   - Escreva mensagens de commit claras e descritivas em português ou inglês
   - Exemplo: `feat: adicionar suporte para agendamento de mensagens`
4. **Push** para seu fork: `git push origin feat/sua-feature`
5. Abra um **Pull Request** com uma descrição detalhada

### Padrões de Código

- Siga as convenções Java (CamelCase para classes, camelCase para métodos)
- Todos os comentários e Javadoc em inglês
- Adicione testes unitários para novas funcionalidades (alvo 80% de cobertura)
- Execute os testes antes de enviar: `./gradlew test`
- Mantenha o código limpo sem comentários desnecessários

### Requisitos de Testes

- Cobertura mínima de 80% do código
- Todos os testes devem passar: `./gradlew build`
- Teste novas funcionalidades minuciosamente com cenários positivos e negativos

## Solução de Problemas

### Chave de API Inválida
```
AraraAuthException: Unauthorized
```
- Verifique se sua chave de API está correta
- Confira se sua chave de API não expirou
- Garanta que a chave tem as permissões apropriadas

### Formato de Número de Telefone Inválido
```
AraraException: Invalid phone number. Must start with 'whatsapp:+'
```
- Números de telefone devem estar no formato: `whatsapp:+[código_país][número]`
- Exemplo: `whatsapp:+5511999998888` (Brasil)

### Timeout de Rede
```
AraraNetworkException: Communication failure with Arara API
```
- Verifique sua conexão com a internet
- Valide se o endpoint da API está acessível
- Aumente o timeout se necessário (ajuste na configuração)

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).

## Suporte

Para dúvidas, problemas ou solicitações de novas funcionalidades, abra uma issue no GitHub ou contate o time Arara em suporte@ararahq.com.
