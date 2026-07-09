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
