# Список заданий тренажёра

> /trainers/{id}/tasks

---

## Алгоритм

1. `SELECT id FROM trainers WHERE id = ?` – проверка существования.
2. `SELECT id, title, type, max_score FROM tasks WHERE trainer_id = ?`

---

### Успешный ответ: `200 OK` (массив заданий)
