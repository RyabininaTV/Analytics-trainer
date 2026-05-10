# Вход пользователя

> /auth/login

## Параметры эндпоинта
Метод: `POST`
Доступ: публичный
Тело запроса: ``LoginRequest` (email/username, password)`

---

## Алгоритм работы с БД:

1. `SELECT id, password_hash, status FROM users WHERE email = ? OR username = ?` – поиск пользователя.
2. Проверка пароля (BCrypt) и статуса (ACTIVE/BLOCKED).
3. Генерация новой пары токенов.
4. (Опционально) `DELETE` старых и `INSERT` нового refresh-токена в `revoked_tokens`.

---

### Успешный ответ:  `200 OK` (accessToken, refreshToken, данные пользователя)
