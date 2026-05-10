# Выход пользователя

> /auth/logout

## Параметры эндпоинта
- Метод: `POST`
- Доступ: USER, ADMIN (требуется токен)
- Тело запроса: `LogoutRequest` (refreshToken)

---

## Алгоритм работы с БД:

1. `INSERT INTO revoked_tokens (token, user_id, expires_at)` – аннулирование refresh-токена.
2. (Опционально) также добавляется access-токен из заголовка `Authorization`.

---

### Успешный ответ: 

`204 No Content`
