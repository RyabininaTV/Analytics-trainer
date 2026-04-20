-- Таблица пользователей
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    created_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email         VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nick          VARCHAR(50),
    full_name     VARCHAR(255),
    role          VARCHAR(50) DEFAULT 'user' CHECK (role IN ('user', 'admin')),
    is_active     BOOLEAN DEFAULT TRUE
);

COMMENT ON COLUMN users.id            IS 'id';
COMMENT ON COLUMN users.created_date  IS 'дата создания';
COMMENT ON COLUMN users.email         IS 'email';
COMMENT ON COLUMN users.password_hash IS 'Хеш пароля';
COMMENT ON COLUMN users.nick          IS 'Логин';
COMMENT ON COLUMN users.full_name     IS 'Полное имя';
COMMENT ON COLUMN users.role          IS 'Роль: админ или пользователь';
COMMENT ON COLUMN users.is_active     IS 'Флаг: вкл,выкл';

-- Таблица "Типы заданий"
CREATE TABLE trainers (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    difficulty          VARCHAR(1) CHECK (difficulty IN ('E','N','H')),
    total_tasks         INTEGER DEFAULT 0,
    total_max_score     INTEGER DEFAULT 0,
    time_limit_seconds  INTEGER,
    sort_order          INTEGER DEFAULT 0,
    is_active           BOOLEAN DEFAULT TRUE
);

COMMENT ON COLUMN trainers.id                 IS 'id';
COMMENT ON COLUMN trainers.name               IS 'Наименование: тестовые; задания на поиск ошибок; открытые задания';
COMMENT ON COLUMN trainers.description        IS 'Описание';
COMMENT ON COLUMN trainers.difficulty         IS 'Уровень сложности: Easy, Normal, Hard';
COMMENT ON COLUMN trainers.total_tasks        IS 'Количество заданий в этом типе задания';
COMMENT ON COLUMN trainers.total_max_score    IS 'Максимальное количество баллов в этом типе задания';
COMMENT ON COLUMN trainers.time_limit_seconds IS 'Время на прохождение этого типа задания';
COMMENT ON COLUMN trainers.sort_order         IS 'Сортировка';
COMMENT ON COLUMN trainers.is_active          IS 'Флаг: вкл,выкл';

-- Таблица задания
CREATE TABLE tasks (
    id             BIGSERIAL PRIMARY KEY,
    trainer_id     BIGINT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    type           VARCHAR(50),
    score          INTEGER DEFAULT 0,
    task_data      JSONB,
    correct_answer JSONB,
    sort_order     INTEGER DEFAULT 0
);

COMMENT ON COLUMN tasks.id             IS 'id';
COMMENT ON COLUMN tasks.trainer_id     IS 'Ссылка на тип задания';
COMMENT ON COLUMN tasks.title          IS 'Заголовок';
COMMENT ON COLUMN tasks.description    IS 'Описание';
COMMENT ON COLUMN tasks.type           IS 'Тип вопроса';
COMMENT ON COLUMN tasks.score          IS 'Очки за вопрос';
COMMENT ON COLUMN tasks.task_data      IS 'json содержащий текс вопроса';
COMMENT ON COLUMN tasks.correct_answer IS 'json содержащий ответ на вопрос';
COMMENT ON COLUMN tasks.sort_order     IS 'Сортировка (для вывода)';

-- Попытки выполнения задач пользователями
CREATE TABLE tasks (
    id             BIGSERIAL PRIMARY KEY,
    trainer_id     BIGINT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    type           VARCHAR(50),
    score          INTEGER DEFAULT 0,
    task_data      JSONB,
    correct_answer JSONB,
    sort_order     INTEGER DEFAULT 0
);

COMMENT ON COLUMN tasks.id             IS 'id';
COMMENT ON COLUMN tasks.trainer_id     IS 'id тренировки (внешний ключ)';
COMMENT ON COLUMN tasks.title          IS 'Заголовок';
COMMENT ON COLUMN tasks.description    IS 'Описание';
COMMENT ON COLUMN tasks.type           IS 'Тип вопроса';
COMMENT ON COLUMN tasks.score          IS 'Очки за вопрос';
COMMENT ON COLUMN tasks.task_data      IS 'json содержащий текст вопроса';
COMMENT ON COLUMN tasks.correct_answer IS 'json содержащий ответ на вопрос';
COMMENT ON COLUMN tasks.sort_order     IS 'Сортировка (для вывода)';

-- Статистика пользователя по задачам
CREATE TABLE user_task_performance (
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    task_id              BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    attempt_count        INTEGER DEFAULT 0,
    successful_attempts  INTEGER DEFAULT 0,
    best_score           DECIMAL(5,2) DEFAULT 0,
    average_time_seconds INTEGER DEFAULT 0,
    last_attempt_at      TIMESTAMP
);

COMMENT ON COLUMN user_task_performance.id                   IS 'id';
COMMENT ON COLUMN user_task_performance.user_id              IS 'Пользователи';
COMMENT ON COLUMN user_task_performance.task_id              IS 'Вопросы';
COMMENT ON COLUMN user_task_performance.attempt_count        IS 'Число попыток по этому вопросу';
COMMENT ON COLUMN user_task_performance.successful_attempts  IS 'Успешных ответов';
COMMENT ON COLUMN user_task_performance.best_score           IS 'Лучший ответ';
COMMENT ON COLUMN user_task_performance.average_time_seconds IS 'Среднее время';
COMMENT ON COLUMN user_task_performance.last_attempt_at      IS 'Время последней попытки';

-- Прогресс пользователя по тренерам
CREATE TABLE user_trainer_progress (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    trainer_id      BIGINT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
    total_score     INTEGER DEFAULT 0,
    tasks_completed INTEGER DEFAULT 0,
    tasks_attempted INTEGER DEFAULT 0,
    current_streak  INTEGER DEFAULT 0,
    best_streak     INTEGER DEFAULT 0,
    started_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity   TIMESTAMP,
    completed_at    TIMESTAMP
);

COMMENT ON COLUMN user_trainer_progress.id              IS 'id';
COMMENT ON COLUMN user_trainer_progress.user_id         IS 'Пользователи';
COMMENT ON COLUMN user_trainer_progress.trainer_id      IS 'Типы заданий';
COMMENT ON COLUMN user_trainer_progress.total_score     IS 'Всего очков по заданию';
COMMENT ON COLUMN user_trainer_progress.tasks_completed IS 'Всего выполнено тестов';
COMMENT ON COLUMN user_trainer_progress.tasks_attempted IS 'Всего попыток выполнения';
COMMENT ON COLUMN user_trainer_progress.current_streak  IS 'Текущий результат баллов';
COMMENT ON COLUMN user_trainer_progress.best_streak     IS 'Лучший результат баллов';
COMMENT ON COLUMN user_trainer_progress.started_at      IS 'Задания начато';
COMMENT ON COLUMN user_trainer_progress.last_activity   IS 'Последняя активность по заданию';
COMMENT ON COLUMN user_trainer_progress.completed_at    IS 'Задание завершено';