# Spring Form Login Sample

Este é um pequeno projeto de exemplo que demonstra um formulário de login com Spring Boot, Spring Security e armazenamento de sessão em Redis (Spring Session). Ele inclui proteção de sessão concorrente (limita quantas sessões ativas um usuário pode ter) e um handler de logout que limpa a sessão do repositório (Redis) quando o usuário faz logout.

Principais recursos
- Login por formulário (padrão estilo `j_security_check`): formulário envia para `/j_security_check` com parâmetros `j_username` e `j_password`.
- Limite de sessões por usuário (configurável em `SecurityConfig`). Atualmente está configurado para 1 sessão por usuário.
- Armazenamento de sessões em Redis usando `spring-session-data-redis` (configuração em `SessionConfig`).
- Logout customizado (`SessionCleanupLogoutHandler`) que tenta remover a sessão do repositório ao deslogar.
- Handlers de login/erro com logs detalhados para debugging (`AuthLoggingSuccessHandler` / `AuthLoggingFailureHandler`).
- Testes unitários para `WebController` e `SessionCleanupLogoutHandler`, e teste de contexto que verifica o `SecurityFilterChain`.

Credenciais de teste
- Usuário: `user` / Senha: `password`
- Admin: `admin` / Senha: `adminpass`

Pré-requisitos
- Java 21
- Maven
- Redis (local ou via Docker)

Executando (modo rápido)

1. Inicie o Redis (uma das opções):

   a) Usando Homebrew (macOS):

   ```bash
   brew install redis    # se necessário
   brew services start redis
   ```

   b) Usando Docker (recomendado, dentro do diretório do projeto há `docker-compose.yml`):

   ```bash
   docker compose up -d
   ```

2. Build e run da aplicação:

```bash
mvn -DskipTests package
java -jar target/spring-form-login-sample-0.0.1-SNAPSHOT.jar
# ou, para desenvolvimento
mvn -DskipTests spring-boot:run
```

3. Acesse em http://localhost:8080

Fluxo de login
- Página de login: `GET /login`
- Formulário (templates/login.html) envia `POST /j_security_check` com campos `j_username` e `j_password`.
- Após login com sucesso, redireciona para `/`.

Logout
- POST para `/perform_logout` (há um form de logout na `home.html`) — o `SessionCleanupLogoutHandler` será acionado e tentará apagar a sessão do repositório Redis (se disponível).

Observando sessões no Redis

No Redis CLI você pode checar as chaves de sessão:

```bash
redis-cli KEYS 'spring:session*'
redis-cli HGETALL spring:session:sessions:<sessionId>
redis-cli KEYS 'spring:session:index:*'
```

Durante o logout (se o repositório estiver presente) você verá um log do tipo:

```
DEBUG ... Cleaning up session from session repository: <sessionId>
```

Testes

Para rodar os testes unitários/integrados adicionados:

```bash
mvn test
# rodar um teste específico
mvn -Dtest=com.froiscardoso.demo.config.SessionCleanupLogoutHandlerTest test
```

O que os testes cobrem
- `WebControllerTest` — assegura comportamento do controlador (`/` e `/login`) usando MockMvc standalone.
- `SessionCleanupLogoutHandlerTest` — testa que `deleteById` é chamado quando a sessão existe e que o handler tolera `null` repository.
- `SecurityConfigContextTest` — inicia um contexto Spring Boot leve e verifica que `SecurityFilterChain` está presente.

Notas e troubleshooting
- Se você ver sessões anônimas no Redis antes do login, isso é esperado: o servidor cria uma sessão para armazenar o SavedRequest e outras informações pré-login.
- Se o login não funcionar, verifique:
  - Os logs (nível DEBUG está ativado para `org.springframework.security` e `com.froiscardoso.demo`).
  - Se o Redis está acessível (host/porta em `application.properties`).
  - Cookies do navegador (`JSESSIONID`) — se o cookie não for enviado, novas sessões serão criadas e o login não terá efeito.
- Para alterar o número máximo de sessões por usuário, edite `SecurityConfig` (método `securityFilterChain`) e ajuste `maximumSessions(...)`.

Arquivos importantes
- `src/main/java/com/froiscardoso/demo/config/SecurityConfig.java` — configuração principal do Spring Security.
- `src/main/java/com/froiscardoso/demo/config/SessionConfig.java` — ativa `@EnableRedisHttpSession`.
- `src/main/java/com/froiscardoso/demo/config/SessionCleanupLogoutHandler.java` — handler que apaga a sessão no logout.
- `src/main/resources/templates/login.html` e `home.html` — páginas Thymeleaf.
- `pom.xml` — dependências (Spring Boot, Spring Security, Spring Session/Redis).

Se quiser, eu posso:
- Re-habilitar CSRF (segurança) e mostrar como postar o token automaticamente no formulário de login.
- Estender o `SessionCleanupLogoutHandler` para remover também os índices por principal do Redis (se você usa índices de principal para buscas de sessão).
- Adicionar testes de integração que usam um Redis em-memory (Testcontainers) para validar o comportamento end-to-end.

---

Se quiser que eu gere um `docker-compose` de testes com Redis e execute um teste end-to-end, me diga e eu preparo os arquivos e o script.
