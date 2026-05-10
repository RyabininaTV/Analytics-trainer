# Создание тренажёра

> /admin/trainers

## Параметры эндпоинта
Метод: `POST`
Доступ: требуют роль ADMIN
Тело запроса: `CreateTrainerRequest (title, description)`

---

## Алгоритм

INSERT INTO trainers (title, description, created_at) VALUES (?, ?, NOW())

---

### Успешный ответ: 200 OK (созданный объект)
