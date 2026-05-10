# Рейтинг по тренажёру

> //leaderboard/trainer/{trainerId}

## Параметры эндпоинта

Метод: `GET`

---

## Алгоритм

```sql
SELECT u.id, u.username, up.total_score
FROM user_progress up
JOIN users u ON up.user_id = u.id
WHERE up.trainer_id = ?
ORDER BY total_score DESC
LIMIT 50
```

---

### Успешный ответ: 

200 OK (массив лидеров по тренажёру)
