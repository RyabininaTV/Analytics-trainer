# Получение всех попыток по конкретному заданию

> /attempts/{taskId}

## Параметры эндпоинта

Метод: `GET`

---

## Алгоритм

```sql
SELECT id, created_at, score, status
FROM attempts
WHERE user_id = ?
  AND task_id = ?
```

---

## Успешный ответ: 

200 OK (массив попыток)
