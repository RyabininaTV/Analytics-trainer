# Получение профиля

> /profile

## Параметры эндпоинта
- Метод: `GET`
- Доступ: эндпоинт защищен (`USER`, `ADMIN`)

### Алгоритм работы с БД:

1. Из JWT-контекста получаем `userId`.
2. `SELECT id, email, username, role, status, created_at FROM users WHERE id = ?`.
3. (Опционально) `SELECT SUM(total_score), SUM(completed_tasks) FROM user_progress WHERE user_id = ?`.

---

### Успешный ответ: `200 OK` (объект профиля)

---

## Параметры эндпоинта
- Метод: `PUT`
- Доступ: эндпоинт защищен (`USER`, `ADMIN`)

---

### Алгоритм работы с БД:

1. `SELECT password_hash FROM users WHERE id = ?` – проверка текущего пароля.
2. Если меняется email – `SELECT COUNT(*) FROM users WHERE email = ? AND id != ?` – проверка уникальности.
3. `UPDATE users SET email = ?, username = ?, password_hash = ? WHERE id = ?`.
4. (При смене пароля) `DELETE FROM revoked_tokens WHERE user_id = ?`.

---

### Успешный ответ: 

`200 OK` (обновлённые данные профиля)
