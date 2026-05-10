# История попыток пользователя

> /attempts/users/attempts

## Параметры эндпоинта

Метод: `GET`
Доступ: публичный (не требует токена)
Тело запроса: `RegisterRequest (email, username, password)`

---

## Алгоритм

```sql
SELECT a.*, t.title, tr.title
FROM attempts a
JOIN tasks t ON a.task_id = t.id
JOIN trainers tr ON t.trainer_id = tr.id
WHERE a.user_id = ?
ORDER BY a.created_at DESC
```

---

### Успешный ответ: 

200 OK (массив попыток)
