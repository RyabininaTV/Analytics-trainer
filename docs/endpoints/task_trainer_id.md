# Получение списка всех заданий по тренажеру

> /tasks?trainer_id={id}

## Параметры эндпоинта

Метод: `GET`

---

## Алгоритм

Если `trainer_id` передан: `SELECT id, title, type, max_score FROM tasks WHERE trainer_id = ?`, иначе все задания.

---

## Успешный ответ: 

`200 OK` (массив заданий)
