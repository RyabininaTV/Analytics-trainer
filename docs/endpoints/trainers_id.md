# Информация о тренажёре

> /trainers/{id}

## Параметры эндпоинта
Метод: `GET`

---

## Алгоритм: 

`SELECT id, title, description FROM trainers WHERE id = ?` (если не найден → 404)

---

### Успешный ответ: 

`200 OK` (объект тренажёра)
