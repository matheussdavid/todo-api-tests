# 🐛 Relatório de Bugs

## Bugs Resolvidos

| # | Endpoint | Bug | Root Cause | Correção | Status |
|---|----------|-----|------------|----------|--------|
| 1 | POST /api/tasks | Status inválido retorna 500 em vez de 400 | `HttpMessageNotReadableException` não tratada | Handler para `HttpMessageNotReadableException` no `GlobalExceptionHandler` | ✅ Resolvido |
| 2 | GET /api/tasks/{id} | ID inválido retorna 500 em vez de 400 | `MethodArgumentTypeMismatchException` não tratada | Handler para `MethodArgumentTypeMismatchException` no `GlobalExceptionHandler` | ✅ Resolvido |
| 3 | DELETE /api/tasks/{id} | ID inválido retorna 500 em vez de 400 | Mesmo root cause do #2 | Mesma correção do #2 | ✅ Resolvido |
| 4 | PUT /api/tasks/{id} | ID inválido retorna 500 em vez de 400 | Mesmo root cause do #2 | Mesma correção do #2 | ✅ Resolvido |
| 5 | PUT /api/tasks/{id} | Status inválido retorna 500 em vez de 400 | Mesmo root cause do #1 | Mesma correção do #1 | ✅ Resolvido |
