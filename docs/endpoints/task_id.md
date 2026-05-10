# Детали задания

> /task/{id}

## Параметры эндпоинта

Метод: `GET`

---

## Алгоритм

1. `SELECT id, type, title, description, max_score FROM tasks WHERE id = ?`
2. Если тип `TEST_OPTION`: `SELECT id, text FROM task_options WHERE task_id = ?`
3. Если тип `ERROR_ITEM`: `SELECT id, text FROM task_error_items WHERE task_id = ?`

---

### Успешный ответ: 

`200 OK` (объект задания с вариантами/фрагментами)
