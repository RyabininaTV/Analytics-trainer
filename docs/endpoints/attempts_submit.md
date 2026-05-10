# Отправка ответа (транзакция)

> /attempts/submit

## Параметры эндпоинта

Метод: `POST`

---

## Алгоритм

1. SELECT type, max_score, trainer_id FROM tasks WHERE id = ?
2. INSERT INTO attempts (user_id, task_id, status, started_at) VALUES (?, ?, 'PENDING', NOW())
3. В зависимости от типа:
- TEST_OPTION: SELECT id, is_correct FROM task_options WHERE task_id = ? → вычисление балла.
- ERROR_ITEM: SELECT id, is_correct FROM task_error_items WHERE task_id = ? → вычисление балла.
- OPEN_TEXT: сохраняем text_answer в attempt_answers, статус PENDING_REVIEW, балл 0.
4. INSERT INTO attempt_answers (attempt_id, selected_option_id, text_answer) VALUES (...)
5. UPDATE attempts SET status = 'COMPLETED', score = ?, completed_at = NOW() WHERE id = ?
6. INSERT INTO user_progress ... ON CONFLICT DO UPDATE – увеличение total_score и completed_tasks

---

### Успешный ответ: 

200 OK
