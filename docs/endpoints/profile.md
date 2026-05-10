# Получение профиля

> /auth/register

## Алгоритм работы с БД:

1. Из JWT-контекста получаем `userId`.
2. `SELECT id, email, username, role, status, created_at FROM users WHERE id = ?`.
3. (Опционально) `SELECT SUM(total_score), SUM(completed_tasks) FROM user_progress WHERE user_id = ?`.

---

### Успешный ответ: `200 OK` (объект профиля)
