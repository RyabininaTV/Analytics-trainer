# Детали попытки

> /attempts/{id}/details

## Параметры эндпоинта
Метод: `GET`

---

## Алгоритм

1. SELECT * FROM attempts WHERE id = ? – проверка принадлежности пользователю.
2. SELECT * FROM attempt_answers WHERE attempt_id = ?

---

### Успешный ответ: 200 OK (детали с ответами)
