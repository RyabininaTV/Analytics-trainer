# Прогресс по тренажёрам

> /progress/trainers

## Параметры эндпоинта

Метод: `GET`

---

## Алгоритм

```sql
SELECT up.*, t.title
FROM user_progress up
JOIN trainers t ON up.trainer_id = t.id
WHERE up.user_id = ?
```

---

### Успешный ответ: 

200 OK (массив прогресса по тренажёрам)
