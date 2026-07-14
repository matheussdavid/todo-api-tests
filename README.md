# todo-api-tests

![API Tests](https://github.com/matheussdavid/todo-api-tests/actions/workflows/ci.yml/badge.svg)
![License](https://img.shields.io/github/license/matheussdavid/todo-api-tests)
![Java](https://img.shields.io/badge/Java-25-orange)

Projeto de automação de testes de API REST para o backend de gerenciamento de tarefas (TO-DO). Desenvolvido com a metodologia **Shift-Left Testing**, onde os cenários de teste são elaborados e implementados em paralelo com o desenvolvimento do backend.

## Funcionalidades Testadas

O projeto contém **22 cenários de teste** cobrindo o fluxo completo de CRUD da API:

| Endpoint | Método | Cenários | Descrição |
|----------|--------|----------|-----------|
| `/api/health` | GET | 1 | Verificação de saúde da API |
| `/api/tasks` | POST | 7 | Criação de tarefas com validações |
| `/api/tasks` | GET | 1 | Listagem de tarefas |
| `/api/tasks/{id}` | GET | 3 | Busca por ID com tratamento de erros |
| `/api/tasks/{id}` | PUT | 6 | Atualização com validações |
| `/api/tasks/{id}` | DELETE | 4 | Exclusão com tratamento de erros |

### Status válidos para tarefas

- `PENDENTE`
- `EM_ANDAMENTO`
- `CONCLUIDA`

## Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|-----------|--------|------------|
| Java | 25 | Linguagem principal |
| Apache Maven | 3.8.7 | Gerenciamento de dependências e build |
| JUnit 5 | 5.11.4 | Framework de testes |
| REST Assured | 5.5.7 | Testes de API REST |
| AssertJ | 3.27.7 | Assertivas fluentes |
| Jackson | 2.22.1 | Serialização/deserialização JSON |
| ExtentReports | 5.1.1 | Geração de relatórios HTML |
| GitHub Actions | - | Pipeline de CI/CD |
| Docker Compose | - | Containerização do backend |

## Pré-requisitos

- Java 25 (recomendado: [Eclipse Temurin](https://adoptium.net/))
- O backend da API deve estar rodando em `http://localhost:8080`

> **Repositório do backend:** [matheussdavid/todo-backend-java](https://github.com/matheussdavid/todo-backend-java)

## Instalação

```bash
# Clone o repositório
git clone https://github.com/matheussdavid/todo-api-tests.git

# Acesse o diretório
cd todo-api-tests
```

## Execução dos Testes

```bash
# Executar todos os testes
./mvnw test

# Executar uma classe de teste específica
./mvnw test -Dtest=PostTaskTest

# Executar um método específico
./mvnw test -Dtest=PostTaskTest#shouldCreateTaskWithValidData
```

> **Importante:** O backend deve estar rodando antes da execução dos testes.

## Relatórios

Após a execução, um relatório HTML é gerado em:

```
target/extent-report/index.html
```

O relatório contém:
- Status de cada cenário (aprovado/reprovado)
- Detalhes de requisição e resposta HTTP
- Tempo de execução
- Agrupamento por classe de teste

## Pipeline CI/CD

O projeto utiliza **GitHub Actions** para automação completa:

1. **Trigger:** Push para `main`, PRs para `main` e eventos `repository_dispatch`
2. **Setup:** Clona o repositório do backend automaticamente
3. **Containerização:** Sobe PostgreSQL + backend via Docker Compose
4. **Validação:** Aguarda o endpoint de health retornar status `UP`
5. **Execução:** Roda todos os testes com Maven
6. **Relatório:** Faz upload do relatório ExtentReports como artifact

## Metodologia

Este projeto foi desenvolvido seguindo a abordagem **Shift-Left Testing**:

- Testes de API foram elaborados antes ou em paralelo com o desenvolvimento do backend
- Cenários cobrem fluxos felizes e tratamento de erros
- Bugs foram identificados e documentados durante o desenvolvimento (5 bugs encontrados e resolvidos)

## Problemas Conhecidos

- **Dependência de estado:** Os testes dependem do estado do banco de dados, sem limpeza automática entre execuções
- **Ausência de anotações:** Testes ainda não possuem `@Tag` e `@DisplayName` para melhor organização
- **Thread-safety:** O gerador de relatórios utiliza `HashMap` em ambiente com potencial paralelismo

## Licença

Este projeto está sob a licença Apache License 2.0. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
