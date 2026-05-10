# Информация о тренажёре

> /trainers/{id}

---

## Алгоритм: `SELECT id, title, description FROM trainers WHERE id = ?` (если не найден → 404)

---

### Успешный ответ: `200 OK` (объект тренажёра)
