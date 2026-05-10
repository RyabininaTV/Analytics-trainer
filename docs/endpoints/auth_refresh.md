# Обновление токенов

> /auth/refresh

## Параметры эндпоинта
- Метод: `POST`
- Доступ: публичный (защита через refresh-токен)
- Тело запроса: `RefreshRequest` (refreshToken)

---

## Алгоритм работы с БД:

1. `SELECT * FROM revoked_tokens WHERE token = ?` – проверка, не аннулирован ли токен.
2. Проверка подписи и срока действия refresh-токена (JWT).
3. Генерация новой пары токенов.
4. `INSERT INTO revoked_tokens (token, user_id, expires_at)` – аннулирование старого токена.
5. `INSERT INTO revoked_tokens (token, user_id, expires_at)` – сохранение нового refresh-токена.

---

### Успешный ответ: `200 OK` (новая пара токенов)
