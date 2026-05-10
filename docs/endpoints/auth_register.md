## Регистрация пользователя

> /auth/register

### Параметры эндпоинта

- Метод: `POST`
- Доступ: публичный (не требует токена)
- Тело запроса: `RegisterRequest (email, username, password)`

---

### Алгоритм

1. `SELECT COUNT(*) FROM users WHERE email = ? OR username = ?` – проверка уникальности.
2. Хеширование пароля (BCrypt) – в памяти.
3. `INSERT INTO users (email, username, password_hash, role, status, created_at)` – создание аккаунта.
4. Генерация JWT-токенов (access + refresh).
5. (Опционально) `INSERT INTO revoked_tokens (token, user_id, expires_at)` – сохранение refresh-токена.

---

### Успешный ответ: 

`201 Created` (объект аккаунта + токены)
