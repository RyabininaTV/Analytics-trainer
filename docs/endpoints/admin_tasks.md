# Создание задания (транзакция)

> /admin/tasks

## Параметры эндпоинта
Метод: `POST`
Доступ: требуют роль ADMIN
Тело запроса: `CreateTaskRequest`

---

## Алгоритм

1. INSERT INTO tasks (trainer_id, type, title, description, max_score, created_at) VALUES (...).
2. Для TEST_OPTION: INSERT INTO task_options (task_id, text, is_correct) VALUES (...).
3. Для ERROR_ITEM: INSERT INTO task_error_items (task_id, text, is_correct) VALUES (...).

---

### Успешный ответ: 200 OK (созданное задание)
