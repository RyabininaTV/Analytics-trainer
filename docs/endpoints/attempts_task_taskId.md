# Попытки по заданию

> /attempts/task/{taskId}

## Параметры эндпоинта
Метод: `GET`

---

## Алгоритм

SELECT id, created_at, score, status FROM attempts WHERE user_id = ? AND task_id = ?

---

### Успешный ответ: 200 OK (массив попыток)
