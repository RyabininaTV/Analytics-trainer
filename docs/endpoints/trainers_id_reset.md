# Сброс прогресса

> /trainers/{id}/reset

## Параметры эндпоинта
Метод: `POST`

---

## Алгоритм (транзакция)

1. `DELETE FROM user_progress WHERE user_id = ? AND trainer_id = ?`
2. (Опционально) `DELETE FROM attempts WHERE user_id = ? AND task_id IN (SELECT id FROM tasks WHERE trainer_id = ?)`

---

### Успешный ответ: `204 No Content`
