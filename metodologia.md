# 📋 Metodologia — Shift-Left Testing

## O que é?

Shift-Left Testing é a prática de **antecipar os testes para o início do ciclo de desenvolvimento**, em vez de testar apenas no final.

### Fluxo tradicional (sem shift-left)

```
[Requisitos] → [Desenvolvimento] → [Testes Manuais] → [Automação]
                                        ↑ QA testa só depois de tudo pronto
```

### Fluxo shift-left (que estamos aplicando)

```
[Planejamento] → [Test Design] → [Desenvolvimento] → [Teste Imediato] → [CI/CD]
      ↑               ↑               ↑                    ↑
  QA pensa nos    QA escreve os    Dev implementa      QA testa cada
  cenários com    cenários de      com base nos        endpoint assim
  o time          teste ANTES      cenários do QA      que fica pronto
```

---

## Nosso processo passo a passo

### 1. Definição dos cenários (QA + Dev)

Antes do dev codar um endpoint, o QA define:

- Quais **status codes** esperar (200, 201, 204, 400, 404, etc.)
- Quais **validações** de campos obrigatórios são necessárias
- Quais **casos de erro** precisam ser tratados
- Qual o **formato do response** (contrato)

### 2. Escrita dos testes (QA)

Com os cenários definidos, o QA já escreve os testes automatizados **antes ou em paralelo** com o desenvolvimento, usando:

- **REST Assured** — requisições HTTP e validações
- **JUnit 5** — execução dos testes
- **AssertJ** — asserções fluentes

### 3. Desenvolvimento (Dev)

O dev implementa o endpoint baseado nos cenários fornecidos pelo QA.

### 4. Teste imediato (QA)

Assim que o endpoint sobe em um ambiente, o QA roda os testes **imediatamente** — não espera o endpoint estar 100% completo.

### 5. Report de bugs (QA)

Bugs encontrados são documentados em `bugs.md`, separados por verbo/endpoint, para serem priorizados pelo time.

---

## Nosso projeto atual

### Estrutura de testes

```
todo-api-tests/
├── pom.xml
├── .github/workflows/ci.yml             # CI/CD (GitHub Actions)
├── bugs.md                              # Bugs encontrados (5 bugs registrados)
├── metodologia.md                       # Este arquivo
├── junit-platform.properties            # Config JUnit 5 (timeout, paralelismo)
└── src/test/java/com/example/taskapiclient/
    ├── TestBase.java                    # Configuração base (URL, helpers)
    ├── health/
    │   └── HealthCheckTest.java         # GET /api/health ✅ (1 cenário)
    ├── tasks/
    │   ├── PostTaskTest.java            # POST /api/tasks ✅ (7 cenários)
    │   ├── GetTasksTest.java            # GET /api/tasks ✅ (1 cenário)
    │   ├── GetTaskByIdTest.java         # GET /api/tasks/{id} ✅ (3 cenários)
    │   ├── PutTaskByIdTest.java         # PUT /api/tasks/{id} ✅ (6 cenários)
    │   └── DeleteTaskByIdTest.java      # DELETE /api/tasks/{id} ✅ (4 cenários)
    └── report/
        ├── ExtentReportExtension.java   # Extension JUnit 5 para lifecycle
        ├── ExtentReportManager.java     # Singleton do relatório
        └── ExtentRestAssuredFilter.java # Log de request/response no relatório
```

### Como rodar os testes

```bash
# Subir a API primeiro
cd ../todo-backend-java
mvn spring-boot:run

# Em outro terminal, rodar os testes
cd ../todo-api-tests
mvn test                           # Todos os testes
mvn test -Dtest=PostTaskTest       # Teste específico
```

### Relatório

O relatório ExtentReports é gerado em `target/extent-report/index.html` após cada execução. Na CI, é feito upload como artifact do GitHub Actions.

---

## Análise QA — Problemas encontrados

### 🔴 Críticos

| # | Problema | Arquivo | Impacto |
|---|---------|---------|---------|
| 1 | **Isolamento de testes comprometido** — `obterTaskId()` depende do estado do banco, retorna task existente OU cria uma. Não há cleanup | `TestBase.java:22-45` | Testes falham dependendo da ordem de execução |
| 2 | **HashMap não thread-safe** — `parentTests` usa `HashMap` mas `junit-platform.properties` configura paralelismo por classe | `ExtentReportManager.java:15` | `ConcurrentModificationException` em paralelo |
| 3 | **Race condition no flush** — `flush()` limpa `parentTests` enquanto outra classe pode estar escrevendo | `ExtentReportManager.java:32-36` | `NullPointerException` ou dados corrompidos |
| 4 | **`filterAdded` não é thread-safe** — `boolean` static sem `volatile` ou sincronização | `TestBase.java:15` | Filtro pode ser adicionado duplicado |

### 🟠 Altos

| # | Problema | Arquivo | Impacto |
|---|---------|---------|---------|
| 5 | **Sem cleanup de dados** — tasks criadas durante testes nunca são removidas, DB acumula lixo | Todas as classes | Testes não são reprodutíveis |
| 6 | **`obterTaskId()` não é determinístico** — retorna task arbitrária com `[0].id` sem ordenação garantida | `TestBase.java:30` | Pode pegar task errada |
| 7 | **`obterTaskId()` sem tratamento de erros** — se GET ou POST falhar, exceção genérica sem mensagem | `TestBase.java:33-44` | Debug difícil |
| 8 | **ID hardcoded = 0 para 404** — se a API mudar auto-increment para começar em 0, quebra | `GetTaskByIdTest:31`, `PutTaskByIdTest:104`, `DeleteTaskByIdTest:26` | Fragilidade |
| 9 | **Typo no body da task** — `"Criada automatic amente"` com espaço quebrando a palavra | `TestBase.java:43` | Dados sujos no banco |

### 🟡 Médios

| # | Problema | Arquivo | Impacto |
|---|---------|---------|---------|
| 10 | **Duplicação massiva** — 3 padrões copiados em 3+ classes (ID inválido, não encontrado, status inválido) | `GetTaskByIdTest`, `PutTaskByIdTest`, `DeleteTaskByIdTest` | Manutenção difícil |
| 11 | **Assertions só verificam chaves** — `containsKeys()` não valida tipos, timestamps, ou enum | `GetTaskByIdTest:25`, `GetTasksTest:29` | Testes fracos |
| 12 | **DELETE não verifica remoção** — só checa 204, não confirma com GET posterior | `DeleteTaskByIdTest:13-22` | Teste incompleto |
| 13 | **`GetTasksTest` só valida primeiro elemento** — `tasks.get(0)` ignora o resto | `GetTasksTest:29` | Coverage parcial |
| 14 | **Sem `@Tag`** — impossível rodar só smoke tests ou filtrar por tipo | Todos os arquivos | CI inflexível |
| 15 | **Sem `@DisplayName`** — relatório mostra nomes camelCase em vez de texto legível | Todos os arquivos | Legibilidade ruim |
| 16 | **Mixed language** — métodos em inglês, dados/erros em português | Projeto inteiro | Inconsistência |
| 17 | **Health check no CI frágil** — `grep -q "UP"` passa mesmo se banco estiver down | `ci.yml:43` | Falso positivo |

### 🔵 Baixos

| # | Problema | Arquivo | Impacto |
|---|---------|---------|---------|
| 18 | **`.idea/` pode estar trackeado** — `.gitignore` só ignora `bugs.md`, `metodologia.md`, `target/` | `.gitignore` | Poluição do repo |
| 19 | **`metodologia.md` com nomes desatualizados** — lista arquivos errados | `metodologia.md:72-77` | Documentação desatualizada |
| 20 | **`getStatus() == null` como checagem** — frágil, pode quebrar se filtro mudar | `ExtentReportExtension.java:39` | Fragilidade |

---

## Pontos de melhoria para refatoração

### Prioridade 1 — Críticos

| # | Ação | Arquivo(s) | Esforço |
|---|------|-----------|---------|
| 1 | **Reescrever `obterTaskId()`** — cada teste cria sua task via `@BeforeEach` e limpa via `@AfterEach` | `TestBase.java` + todas as classes | Alto |
| 2 | **`HashMap` → `ConcurrentHashMap`** em `parentTests` | `ExtentReportManager.java:15` | Baixo |
| 3 | **Remover `parentTests.clear()` do `flush()`** — deixar o GC cuidar | `ExtentReportManager.java:36` | Baixo |
| 4 | **Tornar `filterAdded` thread-safe** — usar `AtomicBoolean` | `TestBase.java:15` | Baixo |

### Prioridade 2 — Altos

| # | Ação | Arquivo(s) | Esforço |
|---|------|-----------|---------|
| 5 | **Extrair helpers para padrões duplicados** — `assertInvalidId()`, `assertNotFound()`, `assertInvalidStatus()` | `TestBase.java` | Médio |
| 6 | **Criar `validTaskBody()`** — eliminar `Map.of(...)` duplicados | `TestBase.java` | Baixo |
| 7 | **ID para 404 usar `Integer.MAX_VALUE`** em vez de 0 | `GetTaskByIdTest`, `PutTaskByIdTest`, `DeleteTaskByIdTest` | Baixo |
| 8 | **DELETE deve verificar remoção** — adicionar GET apos DELETE assertando 404 | `DeleteTaskByIdTest.java` | Baixo |

### Prioridade 3 — Médios

| # | Ação | Arquivo(s) | Esforço |
|---|------|-----------|---------|
| 9 | **Adicionar `@DisplayName`** em todos os métodos de teste | Todos os arquivos de teste | Baixo |
| 10 | **Teste para lista vazia** no GET /api/tasks | `GetTasksTest.java` | Baixo |
| 11 | **Assertions estruturais** — validar tipos de `id`, formato ISO-8601 em timestamps, valores de enum | Testes de GET/POST | Médio |
| 12 | **Adicionar `@Tag`** — `smoke`, `regression`, `health`, `crud` | Todos os arquivos | Baixo |
| 13 | **Health check no CI robusto** — parsear JSON em vez de `grep` | `ci.yml` | Baixo |

### Prioridade 4 — Futuras

| # | Ação | Arquivo(s) | Esforço |
|---|------|-----------|---------|
| 14 | **Edge cases** — limites de tamanho, caracteres especiais, SQL injection, tipos errados | Novos testes | Médio |
| 15 | **Testes de performance** — validação de tempo de resposta | Novos testes | Baixo |
| 16 | **Coverage report** — configurar Jacoco | `pom.xml` | Baixo |
| 17 | **Unificar idioma** — decidir PT ou EN e manter consistência | Projeto inteiro | Baixo |

---

## Status atual

| Métrica | Valor |
|---------|-------|
| Testes implementados | 22 |
| Bugs encontrados | 5 (todos resolvidos) |
| Endpoints cobertos | 5/5 (Módulo 2 completo) |
| Relatório | ExtentReports Spark com agrupamento por classe |
| CI/CD | GitHub Actions com pipeline completa |
