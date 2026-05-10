# Глобальный рейтинг

> /leaderboard

## Параметры эндпоинта

Метод: `GET`

---

## Алгоритм

```sql
SELECT u.id, u.username, SUM(up.total_score) AS total
FROM user_progress up
JOIN users u ON up.user_id = u.id
GROUP BY u.id, u.username
ORDER BY total DESC
LIMIT 50
```

---

### Успешный ответ: 

200 OK (массив лидеров)
