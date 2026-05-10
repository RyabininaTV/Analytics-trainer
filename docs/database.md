# 📊 Документация по базе данных

## Общая структура

База данных спроектирована для платформы аналитического тренажёра. Основные сущности:

- **Пользователи** — регистрация, роли, статусы
- **Тренажёры** — наборы заданий по темам
- **Задания** — тесты, поиск ошибок, открытые вопросы
- **Попытки** — решения заданий пользователями
- **Прогресс** — агрегированная статистика по пользователям и тренажёрам

---

## 🏷️ Типы данных (ENUM)

| Тип | Значения | Описание |
|-----|----------|----------|
| `user_role_enum` | `USER`, `ADMIN` | Роли пользователей |
| `user_status_enum` | `ACTIVE`, `BLOCKED` | Статусы пользователей |
| `task_type_enum` | `TEST`, `ERROR_FIND`, `OPEN` | Типы заданий |
| `attempt_status_enum` | `IN_PROGRESS`, `SUBMITTED`, `CHECKED`, `REJECTED` | Статусы попыток |
| `answer_type_enum` | `TEST_OPTION`, `ERROR_ITEM`, `OPEN_TEXT` | Типы ответов внутри попытки |

---

## 📋 Таблицы

### 1. `users` – пользователи системы

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | Уникальный ID пользователя |
| `email` | varchar(255) | NOT NULL, UNIQUE | Email (логин) |
| `username` | varchar(100) | NOT NULL, UNIQUE | Отображаемое имя |
| `password_hash` | varchar(255) | NOT NULL | Хэш пароля |
| `role` | user_role_enum | NOT NULL, DEFAULT 'USER' | Роль: USER / ADMIN |
| `status` | user_status_enum | NOT NULL, DEFAULT 'ACTIVE' | Статус: ACTIVE / BLOCKED |
| `created_at` | timestamp | NOT NULL, DEFAULT now() | Дата регистрации |
| `updated_at` | timestamp | NOT NULL, DEFAULT now() | Дата последнего обновления |

---

### 2. `revoked_tokens` – отозванные JWT-токены

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `token_id` | varchar(100) | PRIMARY KEY | JWT ID (claim jti) |
| `expires_at` | timestamp | NOT NULL | Дата истечения токена |
| `created_at` | timestamp | NOT NULL, DEFAULT now() | Дата отзыва |

---

### 3. `trainers` – тренажёры

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | ID тренажёра |
| `title` | varchar(255) | NOT NULL | Название тренажёра |
| `description` | text | — | Описание тренажёра |
| `difficulty_level` | varchar(30) | CHECK (EASY/MEDIUM/HARD) | Уровень сложности |
| `is_active` | boolean | NOT NULL, DEFAULT true | Доступен ли тренажёр |
| `created_at` | timestamp | NOT NULL, DEFAULT now() | Дата создания |
| `updated_at` | timestamp | NOT NULL, DEFAULT now() | Дата обновления |

**Связи:** один тренажёр может содержать много заданий (`tasks`).

---

### 4. `tasks` – задания

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | ID задания |
| `trainer_id` | bigint | NOT NULL, FK → `trainers(id)` ON DELETE CASCADE | Какому тренажёру принадлежит задание |
| `task_type` | task_type_enum | NOT NULL | Тип задания |
| `title` | varchar(255) | NOT NULL | Название задания |
| `description` | text | NOT NULL | Условие задания |
| `content` | text | — | Дополнительный контент (кейс, артефакт и т.д.) |
| `max_score` | integer | NOT NULL, CHECK (≥ 0) | Максимальный балл за задание |
| `is_active` | boolean | NOT NULL, DEFAULT true | Доступно ли задание |
| `auto_check_enabled` | boolean | NOT NULL, DEFAULT false | Автоматическая проверка |
| `created_at` | timestamp | NOT NULL, DEFAULT now() | Дата создания |
| `updated_at` | timestamp | NOT NULL, DEFAULT now() | Дата обновления |

**Связи:**
- задание принадлежит одному тренажёру (`trainer_id`)
- у одного задания может быть много вариантов ответов (`task_options`) или элементов ошибок (`task_error_items`)
- у одного задания может быть много попыток (`attempts`)

---

### 5. `task_options` – варианты ответов (для типа TEST)

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | ID варианта ответа |
| `task_id` | bigint | NOT NULL, FK → `tasks(id)` ON DELETE CASCADE | Ссылка на задание |
| `option_text` | text | NOT NULL | Текст варианта ответа |
| `is_correct` | boolean | NOT NULL | Правильный ли вариант |

---

### 6. `task_error_items` – элементы для заданий на поиск ошибок (тип ERROR_FIND)

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | ID элемента |
| `task_id` | bigint | NOT NULL, FK → `tasks(id)` ON DELETE CASCADE | Ссылка на задание |
| `fragment_text` | text | NOT NULL | Текст фрагмента |
| `is_error` | boolean | NOT NULL | Содержит ли фрагмент ошибку |
| `explanation` | text | — | Пояснение (почему ошибка/почему нет) |

---

### 7. `attempts` – попытки выполнения заданий

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | ID попытки |
| `user_id` | bigint | NOT NULL, FK → `users(id)` ON DELETE CASCADE | Кто выполнял |
| `task_id` | bigint | NOT NULL, FK → `tasks(id)` ON DELETE CASCADE | Какое задание |
| `started_at` | timestamp | NOT NULL, DEFAULT now() | Время начала |
| `submitted_at` | timestamp | — | Время отправки на проверку |
| `status` | attempt_status_enum | NOT NULL, DEFAULT 'IN_PROGRESS' | Статус попытки |
| `score` | integer | CHECK (≥ 0) | Полученные баллы |
| `max_score_snapshot` | integer | NOT NULL, CHECK (≥ 0) | Максимальный балл на момент прохождения |
| `is_correct` | boolean | — | Полностью правильно? |
| `auto_checked` | boolean | NOT NULL, DEFAULT false | Проверено автоматически |
| `needs_manual_review` | boolean | NOT NULL, DEFAULT false | Требуется ручная проверка |
| `reviewer_comment` | text | — | Комментарий проверяющего |
| `reviewed_at` | timestamp | — | Дата проверки |

**Связи:** одна попытка может содержать несколько ответов (`attempt_answers`).

---

### 8. `attempt_answers` – конкретные ответы пользователя

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | ID ответа |
| `attempt_id` | bigint | NOT NULL, FK → `attempts(id)` ON DELETE CASCADE | К какой попытке относится |
| `answer_type` | answer_type_enum | NOT NULL | Тип ответа |
| `selected_option_id` | bigint | FK → `task_options(id)` ON DELETE SET NULL | Для TEST_OPTION |
| `selected_error_item_id` | bigint | FK → `task_error_items(id)` ON DELETE SET NULL | Для ERROR_ITEM |
| `text_answer` | text | — | Для OPEN_TEXT |

**Ограничение целостности (CHECK):** в зависимости от `answer_type` заполняется только одно поле:
- `TEST_OPTION` → заполнен `selected_option_id`
- `ERROR_ITEM` → заполнен `selected_error_item_id`
- `OPEN_TEXT` → заполнен `text_answer`

---

### 9. `user_progress` – агрегированный прогресс пользователя по тренажёру

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | bigserial | PRIMARY KEY | ID записи прогресса |
| `user_id` | bigint | NOT NULL, FK → `users(id)` ON DELETE CASCADE | Пользователь |
| `trainer_id` | bigint | NOT NULL, FK → `trainers(id)` ON DELETE CASCADE | Тренажёр |
| `completed_tasks_count` | integer | NOT NULL, DEFAULT 0, CHECK (≥ 0) | Количество завершённых заданий |
| `total_tasks_count` | integer | NOT NULL, DEFAULT 0, CHECK (≥ 0) | Всего заданий в тренажёре |
| `total_score` | integer | NOT NULL, DEFAULT 0, CHECK (≥ 0) | Сумма набранных баллов |
| `completion_percent` | numeric(5,2) | NOT NULL, DEFAULT 0, CHECK (0–100) | Процент прохождения |
| `last_activity_at` | timestamp | — | Дата последней активности |

**Уникальность:** пара `(user_id, trainer_id)` уникальна.

**Примечание:** процент прохождения вычисляется по формуле: 
completion_percent = (completed_tasks_count * 100.0) / total_tasks_count
(при `total_tasks_count > 0`, иначе 0).

---

## 🔗 Схема связей таблиц

erDiagram
    %% Сущности
    USERS {
      integer id
    }
    TRAINERS {
      integer id
    }
    TASKS {
      integer id
      string type
    }
    ATTEMPTS {
      integer id
    }
    ATTEMPT_ANSWERS {
      integer id
      string answer_type
    }
    TASK_OPTIONS {
      integer id
    }
    TASK_ERROR_ITEMS {
      integer id
    }
    USER_PROGRESS {
      integer id
    }
    REVOKED_TOKENS {
      integer id
    }

    %% Основные связи
    USERS ||--o{ ATTEMPTS : "один пользователь → много попыток"
    USERS ||--o{ USER_PROGRESS : "один пользователь → прогресс по тренажёрам"
    USERS ||--o{ REVOKED_TOKENS : "один пользователь → много отозванных токенов"

    TRAINERS ||--o{ TASKS : "один тренажёр → много заданий"
    TRAINERS ||--o{ USER_PROGRESS : "один тренажёр → прогресс многих пользователей"

    TASKS ||--o{ ATTEMPTS : "одно задание → много попыток"
    TASKS ||--o{ TASK_OPTIONS : "одно задание → много вариантов ответов (только для type=TEST)"
    TASKS ||--o{ TASK_ERROR_ITEMS : "одно задание → много фрагментов (только для type=ERROR_FIND)"

    ATTEMPTS ||--o{ ATTEMPT_ANSWERS : "одна попытка → много ответов"

    %% Условные связи для типов ответов в attempt_answers
    ATTEMPT_ANSWERS }o--|| TASK_OPTIONS : "при answer_type = 'TEST_OPTION'"
    ATTEMPT_ANSWERS }o--|| TASK_ERROR_ITEMS : "при answer_type = 'ERROR_ITEM'"


┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   ОСНОВНЫЕ СВЯЗИ                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                               │
│   users ──────┬────── attempts                      users ──────┬────── user_progress         │
│               │                                                  │                            │
│               │   один пользователь → много попыток              │   один пользователь →       │
│               │                                                  │   прогресс по тренажёрам    │
│               │                                                                               │
│               └────── revoked_tokens                                                            │
│                         один пользователь → много отозванных токенов                           │
│                                                                                               │
│                                                                                               │
│   trainers ──┬────── tasks                          trainers ──┬────── user_progress          │
│              │                                                 │                              │
│              │   один тренажёр → много заданий                 │   один тренажёр →            │
│              │                                                 │   прогресс многих пользователей│
│              │                                                                               │
│                                                                                               │
│   tasks ─────┬────── attempts                      tasks ──────┬────── task_options           │
│              │                                                 │                              │
│              │   одно задание → много попыток                  │   одно задание →             │
│              │                                                 │   много вариантов ответов    │
│              │                                                 │   (только для типа TEST)     │
│              │                                                                               │
│              └────── task_error_items                                                         │
│                        одно задание → много фрагментов                                        │
│                        (только для типа ERROR_FIND)                                          │
│                                                                                               │
│                                                                                               │
│   attempts ──┬────── attempt_answers                                                          │
│              │                                                                               │
│              │   одна попытка → много ответов                                                 │
│              │                                                                               │
│              │                                                                               │
│   attempt_answers ──┬────── task_options                                                      │
│                     │   (при answer_type = 'TEST_OPTION')                                     │
│                     │                                                                         │
│                     └────── task_error_items                                                  │
│                           (при answer_type = 'ERROR_ITEM')                                    │
│                                                                                               │
└─────────────────────────────────────────────────────────────────────────────────────────────┘

## 📋 Детальные схемы таблиц

### 1. Таблица users

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                     USERS                                                    │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ Уникальный ID пользователя                        │
│ email       │ varchar(255) (UK)         │ Email (логин)                                     │
│ username    │ varchar(100) (UK)         │ Отображаемое имя                                  │
│ password_hash│ varchar(255)              │ Хэш пароля                                        │
│ role        │ user_role_enum            │ Роль: USER или ADMIN                              │
│ status      │ user_status_enum          │ Статус: ACTIVE или BLOCKED                        │
│ created_at  │ timestamp                 │ Дата регистрации                                  │
│ updated_at  │ timestamp                 │ Дата последнего обновления                        │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** users → attempts (1:N), users → user_progress (1:N), users → revoked_tokens (1:N)

### 2. Таблица trainers

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    TRAINERS                                                  │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ ID тренажёра                                      │
│ title       │ varchar(255)              │ Название тренажёра                                │
│ description │ text                      │ Описание тренажёра                                │
│ difficulty_level│ varchar(30)           │ Уровень: EASY / MEDIUM / HARD                     │
│ is_active   │ boolean                   │ Доступен ли тренажёр (true/false)                 │
│ created_at  │ timestamp                 │ Дата создания                                     │
│ updated_at  │ timestamp                 │ Дата обновления                                   │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** trainers → tasks (1:N), trainers → user_progress (1:N)

### 3. Таблица tasks

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                      TASKS                                                   │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ ID задания                                        │
│ trainer_id  │ bigint (FK)               │ Какому тренажёру принадлежит                      │
│ task_type   │ task_type_enum            │ Тип: TEST / ERROR_FIND / OPEN                     │
│ title       │ varchar(255)              │ Название задания                                  │
│ description │ text                      │ Условие задания                                   │
│ content     │ text                      │ Дополнительный контент (кейс, артефакт)           │
│ max_score   │ integer                   │ Максимальный балл                                 │
│ is_active   │ boolean                   │ Доступно ли задание                               │
│ auto_check_enabled│ boolean              │ Автоматическая проверка включена?                 │
│ created_at  │ timestamp                 │ Дата создания                                     │
│ updated_at  │ timestamp                 │ Дата обновления                                   │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** tasks → trainers (N:1), tasks → attempts (1:N), tasks → task_options (1:N), tasks → task_error_items (1:N)

### 4. Таблица task_options

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                  TASK_OPTIONS                                               │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ ID варианта ответа                               │
│ task_id     │ bigint (FK)               │ К какому тестовому заданию                        │
│ option_text │ text                      │ Текст варианта ответа                            │
│ is_correct  │ boolean                   │ Правильный ли вариант                             │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** task_options → tasks (N:1), task_options → attempt_answers (1:N)

### 5. Таблица task_error_items

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                               TASK_ERROR_ITEMS                                             │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ ID элемента                                       │
│ task_id     │ bigint (FK)               │ К заданию типа ERROR_FIND                         │
│ fragment_text│ text                     │ Текст фрагмента                                   │
│ is_error    │ boolean                   │ Содержит ли ошибку                                │
│ explanation │ text                      │ Пояснение (почему ошибка/верно)                   │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** task_error_items → tasks (N:1), task_error_items → attempt_answers (1:N)

### 6. Таблица attempts

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                     ATTEMPTS                                                │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ ID попытки                                        │
│ user_id     │ bigint (FK)               │ Какой пользователь                                 │
│ task_id     │ bigint (FK)               │ Какое задание                                     │
│ started_at  │ timestamp                 │ Время начала                                      │
│ submitted_at│ timestamp                 │ Время отправки на проверку                        │
│ status      │ attempt_status_enum       │ Статус: IN_PROGRESS/SUBMITTED/CHECKED/REJECTED    │
│ score       │ integer                   │ Набранные баллы                                   │
│ max_score_snapshot│ integer             │ Максимальный балл на момент прохождения           │
│ is_correct  │ boolean                   │ Полностью верно?                                  │
│ auto_checked│ boolean                   │ Проверено автоматически?                          │
│ needs_manual_review│ boolean            │ Нужна ручная проверка?                            │
│ reviewer_comment│ text                  │ Комментарий проверяющего                          │
│ reviewed_at │ timestamp                 │ Дата проверки                                     │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** attempts → users (N:1), attempts → tasks (N:1), attempts → attempt_answers (1:N)

### 7. Таблица attempt_answers

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                 ATTEMPT_ANSWERS                                            │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ ID ответа                                         │
│ attempt_id  │ bigint (FK)               │ К какой попытке                                   │
│ answer_type │ answer_type_enum          │ Тип ответа: TEST_OPTION / ERROR_ITEM / OPEN_TEXT │
│ selected_option_id│ bigint (FK)         │ Выбранный вариант (для TEST_OPTION)               │
│ selected_error_item_id│ bigint (FK)     │ Выбранный элемент (для ERROR_ITEM)                │
│ text_answer │ text                      │ Текстовый ответ (для OPEN_TEXT)                   │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** attempt_answers → attempts (N:1), attempt_answers → task_options (N:1), attempt_answers → task_error_items (N:1)

### 8. Таблица user_progress
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                 USER_PROGRESS                                              │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ id          │ bigserial (PK)            │ ID записи прогресса                              │
│ user_id     │ bigint (FK)               │ Пользователь                                      │
│ trainer_id  │ bigint (FK)               │ Тренажёр                                          │
│ completed_tasks_count│ integer          │ Количество завершённых заданий                    │
│ total_tasks_count│ integer              │ Всего заданий в тренажёре                         │
│ total_score │ integer                   │ Сумма набранных баллов                            │
│ completion_percent│ numeric(5,2)        │ Процент прохождения (0-100)                       │
│ last_activity_at│ timestamp             │ Дата последней активности                         │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** user_progress → users (N:1), user_progress → trainers (N:1)

### 9. Таблица revoked_tokens

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                 REVOKED_TOKENS                                             │
├─────────────┬───────────────────────────┬───────────────────────────────────────────────────┤
│ Поле        │ Тип                       │ Описание                                          │
├─────────────┼───────────────────────────┼───────────────────────────────────────────────────┤
│ token_id    │ varchar(100) (PK)         │ JWT ID (claim jti)                               │
│ expires_at  │ timestamp                 │ Дата истечения токена                            │
│ created_at  │ timestamp                 │ Дата отзыва                                       │
└─────────────┴───────────────────────────┴───────────────────────────────────────────────────┘
**Связи:** revoked_tokens → users (N:1) — если привязан к пользователю, иначе независимая.

