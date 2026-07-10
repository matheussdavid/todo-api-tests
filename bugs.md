# 🐛 Relatório de Bugs

## POST /api/tasks

### Bug #1 — Status inválido retorna 500 em vez de 400

**Descrição:** Ao enviar um status inválido (ex: `"TESTE"`) no campo `status`, a API retorna **500 Internal Server Error** em vez de **400 Bad Request** com uma mensagem informando os valores aceitos (`PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`).

**Steps para reproduzir:**
```json
POST /api/tasks
{
  "title": "Tarefa teste",
  "status": "TESTE"
}
```

**Resultado atual:** `500 Internal Server Error` (body genérico)

**Resultado esperado:** `400 Bad Request` com mensagem listando os status aceitos.

---

## GET /api/tasks/{id}

### Bug #2 — ID inválido retorna 500 em vez de 400

**Descrição:** Ao enviar caracteres especiais ou texto no lugar do ID (ex: `"ab"`), a API retorna **500 Internal Server Error** em vez de **400 Bad Request**.

**Steps para reproduzir:**
```http
GET /api/tasks/ab
```

**Resultado atual:** `500 Internal Server Error`

**Resultado esperado:** `400 Bad Request` com mensagem "ID Inválido, tente novamente".

---

## DELETE /api/tasks/{id}

### Bug #3 — ID inválido retorna 500 em vez de 400

**Descrição:** Ao enviar caracteres especiais ou texto no lugar do ID (ex: `"ab"`), a API retorna **500 Internal Server Error** em vez de **400 Bad Request**.

**Steps para reproduzir:**
```http
DELETE /api/tasks/ab
```

**Resultado atual:** `500 Internal Server Error`

**Resultado esperado:** `400 Bad Request` com mensagem "ID Inválido, tente novamente".

> **Nota:** Mesmo root cause do Bug #2 — `MethodArgumentTypeMismatchException` não tratada no `GlobalExceptionHandler`.

---

## PUT /api/tasks/{id}

### Bug #4 — ID inválido retorna 500 em vez de 400

**Descrição:** Ao enviar caracteres especiais ou texto no lugar do ID (ex: `"ab"`), a API retorna **500 Internal Server Error** em vez de **400 Bad Request**.

**Steps para reproduzir:**
```http
PUT /api/tasks/ab
Content-Type: application/json

{
  "title": "titulo atualizado",
  "description": "descricao atualizada",
  "status": "EM_ANDAMENTO"
}
```

**Resultado atual:** `500 Internal Server Error`

**Resultado esperado:** `400 Bad Request` com mensagem "ID Inválido, tente novamente".

> **Nota:** Mesmo root cause dos Bugs #2 e #3.

---

### Bug #5 — Status inválido retorna 500 em vez de 400

**Descrição:** Ao enviar um status inválido (ex: `"TESTE"`) no campo `status`, a API retorna **500 Internal Server Error** em vez de **400 Bad Request**.

**Steps para reproduzir:**
```http
PUT /api/tasks/{id}
Content-Type: application/json

{
  "title": "titulo atualizado",
  "description": "descricao atualizada",
  "status": "TESTE"
}
```

**Resultado atual:** `500 Internal Server Error`

**Resultado esperado:** `400 Bad Request` com mensagem listando os status aceitos.

> **Nota:** Mesmo root cause do Bug #1.
