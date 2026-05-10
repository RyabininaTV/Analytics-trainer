# Редактирование тренажёра

> /admin/trainers/{id}

## Параметры эндпоинта
Метод: `PUT`
Доступ: требуют роль ADMIN
Тело запроса: `UpdateTrainerRequest`

---

## Алгоритм

1. SELECT id FROM trainers WHERE id = ? – проверка существования.
2. UPDATE trainers SET title = ?, description = ? WHERE id = ?

---

### Успешный ответ: 200 OK (обновлённый объект)
