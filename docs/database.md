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
    users ||--o{ attempts : "имеет попытки"
    users ||--o{ user_progress : "имеет прогресс"
    users ||--o{ revoked_tokens : "имеет отозванные токены"

    trainers ||--o{ tasks : "содержит задания"
    trainers ||--o{ user_progress : "отслеживает прогресс"

    tasks ||--o{ attempts : "выполняется пользователями"
    tasks ||--o{ task_options : "имеет варианты ответов"
    tasks ||--o{ task_error_items : "имеет элементы ошибок"

    attempts ||--o{ attempt_answers : "содержит ответы"

    attempt_answers |o--o| task_options : "ссылается на вариант"
    attempt_answers |o--o| task_error_items : "ссылается на элемент"

## 🔗 Схема базы данных

erDiagram
    %% ===== ОСНОВНЫЕ СУЩНОСТИ =====
    
    users ||--o{ attempts : "имеет попытки"
    trainers ||--o{ tasks : "содержит задания"
    tasks ||--o{ task_options : "имеет варианты ответов"
    tasks ||--o{ task_error_items : "имеет элементы ошибок"
    tasks ||--o{ attempts : "выполняется пользователями"
    attempts ||--o{ attempt_answers : "содержит ответы"
    users ||--o{ user_progress : "имеет прогресс"
    trainers ||--o{ user_progress : "отслеживает прогресс"

    %% ===== ТАБЛИЦА: ПОЛЬЗОВАТЕЛИ =====
    users {
        bigserial id PK "Уникальный ID пользователя"
        varchar email UK "Email логин"
        varchar username UK "Отображаемое имя"
        varchar password_hash "Хэш пароля"
        user_role_enum role "Роль USER или ADMIN"
        user_status_enum status "Статус ACTIVE или BLOCKED"
        timestamp created_at "Дата создания"
        timestamp updated_at "Дата обновления"
    }

    %% ===== ТАБЛИЦА: ТРЕНАЖЁРЫ =====
    trainers {
        bigserial id PK "ID тренажёра"
        varchar title "Название тренажёра"
        text description "Описание"
        varchar difficulty_level "Уровень EASY MEDIUM HARD"
        boolean is_active "Доступен ли тренажёр"
        timestamp created_at "Дата создания"
        timestamp updated_at "Дата обновления"
    }

    %% ===== ТАБЛИЦА: ЗАДАНИЯ =====
    tasks {
        bigserial id PK "ID задания"
        bigint trainer_id FK "Какому тренажёру принадлежит"
        task_type_enum task_type "Тип TEST ERROR_FIND OPEN"
        varchar title "Название задания"
        text description "Условие задания"
        text content "Дополнительный контент"
        integer max_score "Максимальный балл за задание"
        boolean is_active "Доступно ли задание"
        boolean auto_check_enabled "Автопроверка включена"
        timestamp created_at "Дата создания"
        timestamp updated_at "Дата обновления"
    }

    %% ===== ТАБЛИЦА: ВАРИАНТЫ ОТВЕТОВ =====
    task_options {
        bigserial id PK "ID варианта ответа"
        bigint task_id FK "К какому тестовому заданию"
        text option_text "Текст варианта"
        boolean is_correct "Правильный ли вариант"
    }

    %% ===== ТАБЛИЦА: ЭЛЕМЕНТЫ ДЛЯ ПОИСКА ОШИБОК =====
    task_error_items {
        bigserial id PK "ID элемента"
        bigint task_id FK "К заданию типа ERROR_FIND"
        text fragment_text "Текст фрагмента"
        boolean is_error "Содержит ошибку"
        text explanation "Пояснение"
    }

    %% ===== ТАБЛИЦА: ПОПЫТКИ =====
    attempts {
        bigserial id PK "ID попытки"
        bigint user_id FK "Какой пользователь"
        bigint task_id FK "Какое задание"
        timestamp started_at "Время начала"
        timestamp submitted_at "Время отправки"
        attempt_status_enum status "Статус попытки"
        integer score "Набранные баллы"
        integer max_score_snapshot "Макс балл на момент прохождения"
        boolean is_correct "Полностью верно"
        boolean auto_checked "Проверено автоматически"
        boolean needs_manual_review "Нужна ручная проверка"
        text reviewer_comment "Комментарий проверяющего"
        timestamp reviewed_at "Дата проверки"
    }

    %% ===== ТАБЛИЦА: ОТВЕТЫ ПОЛЬЗОВАТЕЛЯ =====
    attempt_answers {
        bigserial id PK "ID ответа"
        bigint attempt_id FK "К какой попытке"
        answer_type_enum answer_type "Тип ответа"
        bigint selected_option_id FK "Выбранный вариант для TEST"
        bigint selected_error_item_id FK "Выбранный элемент для ERROR_FIND"
        text text_answer "Текстовый ответ для OPEN"
    }

    %% ===== ТАБЛИЦА: АГРЕГИРОВАННЫЙ ПРОГРЕСС =====
    user_progress {
        bigserial id PK "ID записи прогресса"
        bigint user_id FK "Пользователь"
        bigint trainer_id FK "Тренажёр"
        integer completed_tasks_count "Количество завершённых заданий"
        integer total_tasks_count "Всего заданий в тренажёре"
        integer total_score "Сумма баллов"
        numeric completion_percent "Процент прохождения 0-100"
        timestamp last_activity_at "Последняя активность"
    }
