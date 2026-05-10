# Создание тренажёра

> /admin/trainers

## Параметры эндпоинта

Метод: `POST`
Доступ: требуют роль ADMIN
Тело запроса: `CreateTrainerRequest (title, description)`

---

## Алгоритм

```sql
INSERT INTO trainers (title, description, created_at)
VALUES (?, ?, NOW())
```

---

## Успешный ответ: 

201 CREATED (созданный объект)
