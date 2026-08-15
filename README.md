# Ordering

Microserviço de **pedidos e checkout** do Algashop.

Responsável por clientes, carrinho de compras, checkout, criação de pedidos e integração com cálculo de frete (Rapidex).

## Stack

- Java 21
- Spring Boot 3.4
- Spring Data JPA
- H2 (desenvolvimento)
- ModelMapper
- JaCoCo (cobertura mínima de 100%)

## Executar localmente

Suba o stub da Rapidex antes de rodar integrações ou a aplicação completa:

```bash
# na raiz do monorepo algashop
docker compose up -d wiremock
```

Em seguida:

```bash
./gradlew bootRun
```

| Recurso | URL |
|---------|-----|
| API | http://localhost:8080 |
| H2 Console | http://localhost:8080/h2-console |
| Rapidex (WireMock) | http://localhost:8780 |

Credenciais padrão do H2: usuário `sa`, senha `123`.

## Testes

```bash
# testes unitários
./gradlew test

# testes de integração (*IT)
./gradlew integrationTest

# suite completa com verificação de cobertura
./gradlew check
```

A task `check` executa testes unitários, integração e falha se a cobertura JaCoCo for inferior a 100%.

Relatórios:

- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

## Arquitetura

```
src/main/java/com/eskcti/algashop/ordering/
├── application/          # casos de uso (checkout, order, customer, cart)
├── domain/               # modelos e serviços de domínio
└── infrastructure/       # persistência, integrações (Rapidex), configuração
```

### Principais capacidades

- **Clientes** — cadastro, consulta, pontos de fidelidade e notificações
- **Carrinho** — gestão de itens e checkout
- **Checkout / Buy Now** — finalização de compra
- **Pedidos** — criação e consulta
- **Frete** — integração com Rapidex via WireMock em desenvolvimento

### Integrações

Configuração em `application.yaml`:

```yaml
algashop:
  integrations:
    shipping.provider: "RAPIDEX"
    rapidex.url: "http://localhost:8780"
```

## Cobertura via monorepo

Na raiz do Algashop:

```bash
python check_coverage.py ordering
```

O script sobe o WireMock automaticamente quando necessário.
