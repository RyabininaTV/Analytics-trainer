# Общий прогресс

> /progress

## Параметры эндпоинта
Метод: `GET`

---

## Алгоритм

SELECT SUM(total_score) AS total, SUM(completed_tasks) AS completed FROM user_progress WHERE user_id = ?

---

### Успешный ответ: 200 OK (объект прогресса)
