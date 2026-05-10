# Получение списка всех тренажеров

> /trainers

## Параметры эндпоинта
- Метод: `GET`
  
---

## Алгоритм: 

`SELECT id, title, description FROM trainers`

---

## Успешный ответ: 

`200 OK` (массив `TrainerResponse`)
