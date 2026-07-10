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
├── bugs.md                              # Bugs encontrados (5 bugs registrados)
├── metodologia.md                       # Este arquivo
└── src/test/java/com/example/taskapiclient/
    ├── TestBase.java                    # Configuração base (URL, helpers)
    ├── health/
    │   └── HealthCheck.java             # GET /api/health ✅ (1 cenário)
    └── tasks/
        ├── PostTask.java                # POST /api/tasks ✅ (7 cenários)
        ├── GetTasks.java                # GET /api/tasks ✅ (1 cenário)
        ├── GetTaskById.java             # GET /api/tasks/{id} ✅ (3 cenários)
        ├── PutTaskById.java             # PUT /api/tasks/{id} ✅ (6 cenários)
        └── DeleteTaskById.java          # DELETE /api/tasks/{id} ✅ (4 cenários)
```

### Como rodar os testes

```bash
# Subir a API primeiro
cd ../todo-backend-java
mvn spring-boot:run

# Em outro terminal, rodar os testes
cd ../todo-api-tests
mvn test                           # Todos os testes
mvn test -Dtest=tasks.PostTask     # Teste específico
```

---

## Pontos de melhoria para refatoração

### 🔴 Prioridade 1 (corrigir em breve)

#### 1. Consistência no tratamento de bugs conhecidos

**Problema:** Alguns testes de bugs conhecidos têm assertions comentados, outros não.

**Exemplo:** `PostTask.shouldNotCreateTaskWithInvalidStatus` espera 400 mas a API retorna 500, porém o assertion não está comentado — o teste passa sem validar a mensagem.

**Solução:** Padronizar: ou comenta todos os assertions de bugs conhecidos, ou descomenta todos e marca o teste com `@Disabled("Bug #X - aguardando correção")`.

#### 2. Bug silencioso no PutTaskById

**Problema:** Linha 171 usa variável `id` que não existe no escopo (deveria ser `taskId`).

**Solução:** Corrigir `id` → `taskId` antes de descomentar o assertion.

#### 3. Teste para lista vazia no GET /api/tasks

**Problema:** `GetTasks` tem apenas o happy path. Falta testar quando não existem tasks.

**Solução:** Adicionar teste que deleta todas as tasks (ou usa banco vazio) e valida que retorna `[]`.

---

### 🟡 Prioridade 2 (próxima iteração)

#### 4. Extrair bodies para constantes

**Problema:** Request bodies são repetidos em vários testes.

**Solução:** Criar métodos helper ou constantes no `TestBase`:

```java
protected Map<String, String> validTaskBody() {
    return Map.of(
        "title", "Tarefa teste",
        "description", "Descrição teste",
        "status", "PENDENTE"
    );
}
```

#### 5. Adicionar @DisplayName

**Problema:** Nomes de métodos são legíveis no código, mas nos relatórios aparecem como `shouldReturn200WhenValidIdIsProvided`.

**Solução:** Usar `@DisplayName("Deve retornar 200 quando ID válido é informado")` para legibilidade.

#### 6. Isolamento de testes

**Problema:** `obterTaskId()` cria dependência entre testes — se um teste deletar a task, o próximo pode falhar.

**Solução:** Usar `@BeforeEach` para criar dados de teste isolados, ou usar banco H2 em memória que é recriado a cada execução.

---

### 🟢 Prioridade 3 (melhorias futuras)

#### 7. Testes de contrato

**Problema:** Testes validam status codes e chaves, mas não validam tipos dos campos.

**Solução:** Adicionar asserções que validam:
- `id` é Long/Integer
- `title` é String
- `createdAt` é String no formato ISO8601

#### 8. Testes de边界 (edge cases)

**Problema:** Falta testar limites e situações incomuns.

**Solução:** Adicionar testes para:
- Title com 255 caracteres (limite máximo)
- Title com 3 caracteres (limite mínimo)
- Caracteres especiais no título (acentos, emojis, HTML)
- SQL injection no título
- Body com tipos errados (title numérico, status numérico)

#### 9. Testes de performance básicos

**Problema:** Não temos testes de tempo de resposta.

**Solução:** Adicionar validação de tempo de resposta:
```java
.then()
    .time(lessThan(1000L)) // resposta em menos de1 segundo
```

#### 10. Coverage report

**Problema:** Não estamos gerando relatório de cobertura.

**Solução:** Configurar Jacoco no `pom.xml` e gerar relatório com:
```bash
mvn clean test jacoco:report
```

---

## Bugs registrados

| # | Endpoint | Bug | Root Cause | Status |
|---|----------|-----|------------|--------|
| 1 | POST /api/tasks | Status inválido retorna 500 em vez de 400 | `HttpMessageNotReadableException` não tratada | ✅ Resolvido |
| 2 | GET /api/tasks/{id} | ID inválido retorna 500 em vez de 400 | `MethodArgumentTypeMismatchException` não tratada | ✅ Resolvido |
| 3 | DELETE /api/tasks/{id} | ID inválido retorna 500 em vez de 400 | Mesmo root cause do #2 | ✅ Resolvido |
| 4 | PUT /api/tasks/{id} | ID inválido retorna 500 em vez de 400 | Mesmo root cause do #2 | ✅ Resolvido |
| 5 | PUT /api/tasks/{id} | Status inválido retorna 500 em vez de 400 | Mesmo root cause do #1 | ✅ Resolvido |

**Nota:** Todos os bugs foram resolvidos com dois handlers no `GlobalExceptionHandler`: um para `MethodArgumentTypeMismatchException` (IDs inválidos) e outro para `HttpMessageNotReadableException` (status inválidos).

---

## Status atual

- **Testes implementados:** 22
- **Bugs encontrados:** 5 (todos resolvidos)
- **Endpoints cobertos:** 5/5 (Módulo 2 completo)
- **Próximo:** Refatoração seguindo os pontos de melhoria acima
