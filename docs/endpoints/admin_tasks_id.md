> /admin/tasks/{id}

# Редактирование задания

## Параметры эндпоинта

Метод: `PUT`
Доступ: требуют роль ADMIN
Тело запроса: `UpdateTaskRequest`

---

## Алгоритм

1. UPDATE tasks SET ... WHERE id = ?
2. DELETE FROM task_options WHERE task_id = ? и INSERT новых вариантов.
3. Аналогично для task_error_items.

---

### Успешный ответ: 

200 OK (обновлённое задание)

---

# Удаление задания (транзакция)

## Параметры эндпоинта

Метод: `DELETE`
Доступ: требуют роль ADMIN

---

## Алгоритм

1. DELETE FROM attempt_answers WHERE attempt_id IN (SELECT id FROM attempts WHERE task_id = ?)
2. DELETE FROM attempts WHERE task_id = ?
3. DELETE FROM task_options WHERE task_id = ? (или task_error_items)
4. DELETE FROM tasks WHERE id = ?

---

### Успешный ответ: 

204 No Content
