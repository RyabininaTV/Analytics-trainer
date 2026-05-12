# 📊 Документация по базе данных

## Общая структура

База данных спроектирована для платформы аналитического тренажёра. Основные сущности:

- **Пользователи** — регистрация, роли, статусы
- **Тренажёры** — наборы заданий по темам
- **Задания** — тесты, поиск ошибок, открытые вопросы
- **Попытки** — решения заданий пользователями
- **Прогресс** — агрегированная статистика по пользователям и тренажёрам

---

## 🔗 Схема базы данных

![Схема связей таблиц](/docs/imgs/database.svg)

---

## 🏷️ Типы данных (ENUM)

| Тип                   | Значения                                          | Описание                    |
|-----------------------|---------------------------------------------------|-----------------------------|
| `user_role_enum`      | `USER`, `ADMIN`                                   | Роли пользователей          |
| `user_status_enum`    | `ACTIVE`, `BLOCKED`                               | Статусы пользователей       |
| `task_type_enum`      | `TEST`, `ERROR_FIND`, `OPEN`                      | Типы заданий                |
| `attempt_status_enum` | `IN_PROGRESS`, `SUBMITTED`, `CHECKED`, `REJECTED` | Статусы попыток             |
| `answer_type_enum`    | `TEST_OPTION`, `ERROR_ITEM`, `OPEN_TEXT`          | Типы ответов внутри попытки |

---

## 📋 Таблицы

### 1. `users` – пользователи системы

| Поле            | Тип              | Ограничения                | Описание                   |
|-----------------|------------------|----------------------------|----------------------------|
| `id`            | bigserial        | PRIMARY KEY                | Уникальный ID пользователя |
| `email`         | varchar(255)     | NOT NULL, UNIQUE           | Email (логин)              |
| `username`      | varchar(100)     | NOT NULL, UNIQUE           | Отображаемое имя           |
| `password_hash` | varchar(255)     | NOT NULL                   | Хэш пароля                 |
| `role`          | user_role_enum   | NOT NULL, DEFAULT 'USER'   | Роль: USER / ADMIN         |
| `status`        | user_status_enum | NOT NULL, DEFAULT 'ACTIVE' | Статус: ACTIVE / BLOCKED   |
| `created_at`    | timestamp        | NOT NULL, DEFAULT now()    | Дата регистрации           |
| `updated_at`    | timestamp        | NOT NULL, DEFAULT now()    | Дата последнего обновления |

---

### 2. `revoked_tokens` – отозванные JWT-токены

| Поле         | Тип          | Ограничения             | Описание              |
|--------------|--------------|-------------------------|-----------------------|
| `token_id`   | varchar(100) | PRIMARY KEY             | JWT ID (claim jti)    |
| `expires_at` | timestamp    | NOT NULL                | Дата истечения токена |
| `created_at` | timestamp    | NOT NULL, DEFAULT now() | Дата отзыва           |

---

### 3. `trainers` – тренажёры

| Поле               | Тип          | Ограничения              | Описание             |
|--------------------|--------------|--------------------------|----------------------|
| `id`               | bigserial    | PRIMARY KEY              | ID тренажёра         |
| `title`            | varchar(255) | NOT NULL                 | Название тренажёра   |
| `description`      | text         | —                        | Описание тренажёра   |
| `difficulty_level` | varchar(30)  | CHECK (EASY/MEDIUM/HARD) | Уровень сложности    |
| `is_active`        | boolean      | NOT NULL, DEFAULT true   | Доступен ли тренажёр |
| `created_at`       | timestamp    | NOT NULL, DEFAULT now()  | Дата создания        |
| `updated_at`       | timestamp    | NOT NULL, DEFAULT now()  | Дата обновления      |

**Связи:** один тренажёр может содержать много заданий (`tasks`).

---

### 4. `tasks` – задания

| Поле                 | Тип            | Ограничения                                     | Описание                                       |
|----------------------|----------------|-------------------------------------------------|------------------------------------------------|
| `id`                 | bigserial      | PRIMARY KEY                                     | ID задания                                     |
| `trainer_id`         | bigint         | NOT NULL, FK → `trainers(id)` ON DELETE CASCADE | Какому тренажёру принадлежит задание           |
| `task_type`          | task_type_enum | NOT NULL                                        | Тип задания                                    |
| `title`              | varchar(255)   | NOT NULL                                        | Название задания                               |
| `description`        | text           | NOT NULL                                        | Условие задания                                |
| `content`            | text           | —                                               | Дополнительный контент (кейс, артефакт и т.д.) |
| `max_score`          | integer        | NOT NULL, CHECK (≥ 0)                           | Максимальный балл за задание                   |
| `is_active`          | boolean        | NOT NULL, DEFAULT true                          | Доступно ли задание                            |
| `auto_check_enabled` | boolean        | NOT NULL, DEFAULT false                         | Автоматическая проверка                        |
| `created_at`         | timestamp      | NOT NULL, DEFAULT now()                         | Дата создания                                  |
| `updated_at`         | timestamp      | NOT NULL, DEFAULT now()                         | Дата обновления                                |

**Связи:**
- задание принадлежит одному тренажёру (`trainer_id`)
- у одного задания может быть много вариантов ответов (`task_options`) или элементов ошибок (`task_error_items`)
- у одного задания может быть много попыток (`attempts`)

---

### 5. `task_options` – варианты ответов (для типа TEST)

| Поле          | Тип       | Ограничения                                  | Описание              |
|---------------|-----------|----------------------------------------------|-----------------------|
| `id`          | bigserial | PRIMARY KEY                                  | ID варианта ответа    |
| `task_id`     | bigint    | NOT NULL, FK → `tasks(id)` ON DELETE CASCADE | Ссылка на задание     |
| `option_text` | text      | NOT NULL                                     | Текст варианта ответа |
| `is_correct`  | boolean   | NOT NULL                                     | Правильный ли вариант |

---

### 6. `task_error_items` – элементы для заданий на поиск ошибок (тип ERROR_FIND)

| Поле            | Тип       | Ограничения                                  | Описание                             |
|-----------------|-----------|----------------------------------------------|--------------------------------------|
| `id`            | bigserial | PRIMARY KEY                                  | ID элемента                          |
| `task_id`       | bigint    | NOT NULL, FK → `tasks(id)` ON DELETE CASCADE | Ссылка на задание                    |
| `fragment_text` | text      | NOT NULL                                     | Текст фрагмента                      |
| `is_error`      | boolean   | NOT NULL                                     | Содержит ли фрагмент ошибку          |
| `explanation`   | text      | —                                            | Пояснение (почему ошибка/почему нет) |

---

### 7. `attempts` – попытки выполнения заданий

| Поле                  | Тип                 | Ограничения                                  | Описание                                |
|-----------------------|---------------------|----------------------------------------------|-----------------------------------------|
| `id`                  | bigserial           | PRIMARY KEY                                  | ID попытки                              |
| `user_id`             | bigint              | NOT NULL, FK → `users(id)` ON DELETE CASCADE | Кто выполнял                            |
| `task_id`             | bigint              | NOT NULL, FK → `tasks(id)` ON DELETE CASCADE | Какое задание                           |
| `started_at`          | timestamp           | NOT NULL, DEFAULT now()                      | Время начала                            |
| `submitted_at`        | timestamp           | —                                            | Время отправки на проверку              |
| `status`              | attempt_status_enum | NOT NULL, DEFAULT 'IN_PROGRESS'              | Статус попытки                          |
| `score`               | integer             | CHECK (≥ 0)                                  | Полученные баллы                        |
| `max_score_snapshot`  | integer             | NOT NULL, CHECK (≥ 0)                        | Максимальный балл на момент прохождения |
| `is_correct`          | boolean             | —                                            | Полностью правильно?                    |
| `auto_checked`        | boolean             | NOT NULL, DEFAULT false                      | Проверено автоматически                 |
| `needs_manual_review` | boolean             | NOT NULL, DEFAULT false                      | Требуется ручная проверка               |
| `reviewer_comment`    | text                | —                                            | Комментарий проверяющего                |
| `reviewed_at`         | timestamp           | —                                            | Дата проверки                           |

**Связи:** одна попытка может содержать несколько ответов (`attempt_answers`).

---

### 8. `attempt_answers` – конкретные ответы пользователя

| Поле                     | Тип              | Ограничения                                     | Описание                  |
|--------------------------|------------------|-------------------------------------------------|---------------------------|
| `id`                     | bigserial        | PRIMARY KEY                                     | ID ответа                 |
| `attempt_id`             | bigint           | NOT NULL, FK → `attempts(id)` ON DELETE CASCADE | К какой попытке относится |
| `answer_type`            | answer_type_enum | NOT NULL                                        | Тип ответа                |
| `selected_option_id`     | bigint           | FK → `task_options(id)` ON DELETE SET NULL      | Для TEST_OPTION           |
| `selected_error_item_id` | bigint           | FK → `task_error_items(id)` ON DELETE SET NULL  | Для ERROR_ITEM            |
| `text_answer`            | text             | —                                               | Для OPEN_TEXT             |

**Ограничение целостности (CHECK):** в зависимости от `answer_type` заполняется только одно поле:
- `TEST_OPTION` → заполнен `selected_option_id`
- `ERROR_ITEM` → заполнен `selected_error_item_id`
- `OPEN_TEXT` → заполнен `text_answer`

---

### 9. `user_progress` – агрегированный прогресс пользователя по тренажёру

| Поле                    | Тип          | Ограничения                                     | Описание                       |
|-------------------------|--------------|-------------------------------------------------|--------------------------------|
| `id`                    | bigserial    | PRIMARY KEY                                     | ID записи прогресса            |
| `user_id`               | bigint       | NOT NULL, FK → `users(id)` ON DELETE CASCADE    | Пользователь                   |
| `trainer_id`            | bigint       | NOT NULL, FK → `trainers(id)` ON DELETE CASCADE | Тренажёр                       |
| `completed_tasks_count` | integer      | NOT NULL, DEFAULT 0, CHECK (≥ 0)                | Количество завершённых заданий |
| `total_tasks_count`     | integer      | NOT NULL, DEFAULT 0, CHECK (≥ 0)                | Всего заданий в тренажёре      |
| `total_score`           | integer      | NOT NULL, DEFAULT 0, CHECK (≥ 0)                | Сумма набранных баллов         |
| `completion_percent`    | numeric(5,2) | NOT NULL, DEFAULT 0, CHECK (0–100)              | Процент прохождения            |
| `last_activity_at`      | timestamp    | —                                               | Дата последней активности      |

**Уникальность:** пара `(user_id, trainer_id)` уникальна.

**Примечание:** процент прохождения вычисляется по формуле: 
completion_percent = (completed_tasks_count * 100.0) / total_tasks_count
(при `total_tasks_count > 0`, иначе 0).
