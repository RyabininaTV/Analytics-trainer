# История прогресса (график)

> /progress/history

## Параметры эндпоинта
Метод: `GET`

---

## Алгоритм

1. SELECT created_at, score FROM attempts WHERE user_id = ? AND status = 'CORRECT' ORDER BY created_at ASC
2. Вычисление накопленной суммы (running total)

---

### Успешный ответ: 

200 OK (массив точек {changed_at, total_score})
