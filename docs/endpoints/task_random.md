# Случайное задание

> /task/random

## Параметры эндпоинта

Метод: `GET`

---

## Алгоритм

1. `SELECT COUNT(*) FROM tasks`
2. Генерация случайного `OFFSET`
3. `SELECT * FROM tasks OFFSET ? LIMIT 1`
4. Подгрузка деталей (аналогично `GET /tasks/{id}`)

---

### Успешный ответ: 

`200 OK` (объект задания)
