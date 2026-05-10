# Прогресс пользователя в тренажёре

> /trainers/{id}/progress

---

## Алгоритм

1. Из контекста `userId`.
2. `SELECT total_score, completed_tasks FROM user_progress WHERE user_id = ? AND trainer_id = ?`

---

### Успешный ответ: `200 OK` (объект прогресса)
