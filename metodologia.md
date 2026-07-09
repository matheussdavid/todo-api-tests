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
├── bugs.md                              # Bugs encontrados
├── metodologia.md                        # Este arquivo
└── src/test/java/com/example/taskapiclient/
    ├── TestBase.java                     # Configuração base (URL, helpers)
    ├── health/
    │   └── HealthCheck.java              # GET /api/health
    └── tasks/
        ├── PostTask.java                 # POST /api/tasks ✅ (7 cenários)
        ├── GetTasks.java                 # GET /api/tasks (pendente)
        ├── GetTaskById.java              # GET /api/tasks/{id} (pendente)
        ├── PutTaskById.java              # PUT /api/tasks/{id} (pendente)
        └── DeleteTaskById.java           # DELETE /api/tasks/{id} (pendente)
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

## Próximos passos

### GET /api/tasks — Cenários propostos

- Listar tasks quando existem registros → 200 + array
- Listar tasks quando não existem registros → 200 + array vazio
- Validar formato dos campos no array (id, title, status, etc.)

### GET /api/tasks/{id} — Cenários propostos

- Buscar task por ID existente → 200 + task no body
- Buscar task por ID inexistente → 404
- Buscar task com ID negativo ou zero → 400

### PUT /api/tasks/{id} — Cenários propostos

- Atualizar task existente com dados válidos → 200
- Atualizar task com título vazio → 400
- Atualizar task inexistente → 404

### DELETE /api/tasks/{id} — Cenários propostos

- Deletar task existente → 204
- Deletar task já deletada → 404
- Deletar task inexistente → 404
