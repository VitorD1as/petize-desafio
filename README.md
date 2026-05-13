# Petize Desafio — API de tarefas (Spring Boot)

API REST para gestão de tarefas com **autenticação JWT**, **Spring Security**, **JPA/Hibernate**, **Flyway** (schema versionado), **OpenAPI/Swagger**, **Actuator** e testes (integração + unitários).

## Requisitos

- **Java 17**
- **Maven 3.9+**
- **MySQL 8** (local ou via Docker Compose)

## Como rodar (local)

1. Crie o banco (ou use `createDatabaseIfNotExist` na URL padrão).
2. Defina credenciais (ou use os padrões abaixo):

| Variável | Descrição | Padrão (`application.yaml`) |
|----------|-----------|------------------------------|
| `SPRING_DATASOURCE_URL` | JDBC MySQL | `jdbc:mysql://localhost:3306/gym_security?createDatabaseIfNotExist=true` |
| `DATABASE_USERNAME` | usuário MySQL | `root` |
| `DATABASE_PASSWORD` | senha MySQL | `root` |
| `JWT_KEY` | segredo HMAC (≥ 32 caracteres recomendado) | valor longo de exemplo no YAML |
| `JWT_EXPIRATION` | TTL do token em ms | `900000` (15 min) |

3. Execute:

```bash
mvn spring-boot:run
```

- **Porta:** `8082`
- **Swagger UI:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)  
- **OpenAPI JSON:** [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)  
- **Health:** [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)  
- **Info:** [http://localhost:8082/actuator/info](http://localhost:8082/actuator/info) (requer autenticação se `show-details` estiver restrito no futuro)

## Docker Compose (app + MySQL)

```bash
docker compose up --build
```

A aplicação sobe após o healthcheck do MySQL. Credenciais do compose: usuário `petize` / senha `petize`, banco `gym_security`.

## CI

O workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) executa `mvn -B verify` em cada push/PR para `main` ou `master`.

## Testes

```bash
mvn test          # apenas testes
mvn verify        # testes + verificações do ciclo de vida
```

- Perfil **`test`**: H2 em memória, Flyway **desligado**, `ddl-auto=create-drop` (ver `src/test/resources/application-test.yaml`).
- **Integração:** segurança (401), fluxo registro/login, OpenAPI público, health, conflito de e-mail, estatísticas.
- **Unitários:** `TarefasService`, `AuthService`, `JwtService` (Mockito / instância isolada).

## API (exemplos `curl`)

Registrar e obter token:

```bash
curl -s -X POST http://localhost:8082/api/auth/register -H "Content-Type: application/json" -d "{\"nome\":\"Ana\",\"email\":\"ana@example.com\",\"senha\":\"senha12345\"}"
```

Login:

```bash
curl -s -X POST http://localhost:8082/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"ana@example.com\",\"senha\":\"senha12345\"}"
```

Listar tarefas (substitua `TOKEN`):

```bash
curl -s http://localhost:8082/api/tarefas -H "Authorization: Bearer TOKEN"
```

Estatísticas por status do usuário autenticado:

```bash
curl -s http://localhost:8082/api/tarefas/estatisticas -H "Authorization: Bearer TOKEN"
```

## Decisões de arquitetura

- **JWT stateless:** adequado a API REST; cliente envia `Authorization: Bearer`.
- **BCrypt** para senha; **Spring Security** com filtro JWT antes do `UsernamePasswordAuthenticationFilter`.
- **Autorização por recurso:** tarefas sempre filtradas pelo usuário logado (`findByIdAndUser_Id` + specifications).
- **Flyway + `ddl-auto: validate`:** schema explícito em produção/desenvolvimento com MySQL; evita drift entre ambientes.
- **Handlers JSON** para 401/403 sem acoplar `ObjectMapper` à config de segurança (compatível com Jackson 2/3 do Spring Boot 4).
- **OpenAPI 3** com esquema Bearer documentado no Swagger UI.

## Limitações assumidas

- **Soft delete:** “deletar” marca status `CANCELADA` (não remove linha).
- **Subtarefas:** criação em lote não está exposta; regra de negócio cobre finalização com subtarefas pendentes quando existirem.
- **Papéis:** apenas `ROLE_USER` (sem admin).
- **Rate limiting / refresh token:** não implementados (escopo de desafio).
- **Segredo JWT em YAML:** apenas para desenvolvimento; em produção use **variável de ambiente** forte e rotacionada.

## Estrutura do projeto

- `api` — controllers, DTOs de entrada, `RestExceptionHandler`, config OpenAPI  
- `business` — serviços e exceções de domínio  
- `infrastructure` — entidades JPA, repositórios, specifications  
- `security` — JWT, filtro, `SecurityFilterChain`, handlers 401/403  
- `db/migration` — scripts Flyway  

## Licença

- `Intuito:` Projeto realizado apenas para aprendizagem, sem intuito de candidatura a vaga.
