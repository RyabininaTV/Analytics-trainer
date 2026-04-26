drop table if exists attempt_answers cascade;
drop table if exists attempts cascade;
drop table if exists task_error_items cascade;
drop table if exists task_options cascade;
drop table if exists user_progress cascade;
drop table if exists tasks cascade;
drop table if exists simulators cascade;
drop table if exists users cascade;

drop type if exists answer_type_enum cascade;
drop type if exists attempt_status_enum cascade;
drop type if exists task_type_enum cascade;
drop type if exists user_status_enum cascade;
drop type if exists user_role_enum cascade;

create type user_role_enum as enum (
    'USER',
    'ADMIN'
);

comment on type user_role_enum is 'роли пользователей в системе';

create type user_status_enum as enum (
    'ACTIVE',
    'BLOCKED'
);

comment on type user_status_enum is 'статусы пользователей в системе';

create type task_type_enum as enum (
    'TEST',
    'ERROR_FIND',
    'OPEN'
);

comment on type task_type_enum is 'типы заданий';

create type attempt_status_enum as enum (
    'IN_PROGRESS',
    'SUBMITTED',
    'CHECKED',
    'REJECTED'
);

comment on type attempt_status_enum is 'статусы попытки выполнения задания';

create type answer_type_enum as enum (
    'TEST_OPTION',
    'ERROR_ITEM',
    'OPEN_TEXT'
);

comment on type answer_type_enum is 'типы ответов пользователя внутри попытки';

create table users (
    id              bigserial primary key,
    email           varchar(255) not null unique,
    username        varchar(100) not null unique,
    password_hash   varchar(255) not null,
    role            user_role_enum not null default 'USER',
    status          user_status_enum not null default 'ACTIVE',
    created_at      timestamp not null default now(),
    updated_at      timestamp not null default now()
);

comment on table users is 'пользователи системы';
comment on column users.id is 'уникальный идентификатор пользователя';
comment on column users.email is 'email пользователя, используется как логин';
comment on column users.username is 'отображаемое имя пользователя';
comment on column users.password_hash is 'хэш пароля пользователя';
comment on column users.role is 'роль пользователя в системе';
comment on column users.status is 'статус пользователя';
comment on column users.created_at is 'дата и время создания пользователя';
comment on column users.updated_at is 'дата и время последнего обновления пользователя';

create table revoked_tokens (
    token_id    varchar(100) primary key,
    expires_at  timestamp not null,
    created_at  timestamp not null default now()
);

comment on table revoked_tokens is 'отозванные jwt токены';
comment on column revoked_tokens.token_id is 'jwt id токена из claim jti';
comment on column revoked_tokens.expires_at is 'дата и время истечения токена';
comment on column revoked_tokens.created_at is 'дата и время отзыва токена';

create table simulators (
    id                  bigserial primary key,
    title               varchar(255) not null,
    description         text,
    difficulty_level    varchar(30),
    is_active           boolean not null default true,
    created_at          timestamp not null default now(),
    updated_at          timestamp not null default now(),

    constraint chk_simulators_difficulty_level check (
        difficulty_level is null or difficulty_level in ('easy', 'medium', 'hard')
    )
);

comment on table simulators is 'тренажёры или логические наборы заданий';
comment on column simulators.id is 'уникальный идентификатор тренажёра';
comment on column simulators.title is 'название тренажёра';
comment on column simulators.description is 'описание тренажёра';
comment on column simulators.difficulty_level is 'уровень сложности тренажёра';
comment on column simulators.is_active is 'признак доступности тренажёра';
comment on column simulators.created_at is 'дата и время создания тренажёра';
comment on column simulators.updated_at is 'дата и время последнего обновления тренажёра';

create table tasks (
    id                  bigserial primary key,
    simulator_id        bigint not null references simulators(id) on delete cascade,
    task_type           task_type_enum not null,
    title               varchar(255) not null,
    description         text not null,
    content             text,
    max_score           integer not null,
    sort_order          integer not null default 0,
    is_active           boolean not null default true,
    auto_check_enabled  boolean not null default false,
    created_at          timestamp not null default now(),
    updated_at          timestamp not null default now(),

    constraint chk_tasks_max_score check (max_score >= 0),
    constraint chk_tasks_sort_order check (sort_order >= 0)
);

comment on table tasks is 'задания внутри тренажёров';
comment on column tasks.id is 'уникальный идентификатор задания';
comment on column tasks.simulator_id is 'идентификатор тренажёра, к которому относится задание';
comment on column tasks.task_type is 'тип задания: TEST, ERROR_FIND, OPEN';
comment on column tasks.title is 'название задания';
comment on column tasks.description is 'текст условия задания';
comment on column tasks.content is 'дополнительный контент задания: кейс, артефакт, текст требования и т.д.';
comment on column tasks.max_score is 'максимальное количество баллов за задание';
comment on column tasks.sort_order is 'порядок отображения задания внутри тренажёра';
comment on column tasks.is_active is 'признак доступности задания';
comment on column tasks.auto_check_enabled is 'признак автоматической проверки задания';
comment on column tasks.created_at is 'дата и время создания задания';
comment on column tasks.updated_at is 'дата и время последнего обновления задания';

create table task_options (
    id          bigserial primary key,
    task_id     bigint not null references tasks(id) on delete cascade,
    option_text text not null,
    is_correct  boolean not null,
    sort_order  integer not null default 0,

    constraint chk_task_options_sort_order check (sort_order >= 0)
);

comment on table task_options is 'варианты ответов для тестовых заданий';
comment on column task_options.id is 'уникальный идентификатор варианта ответа';
comment on column task_options.task_id is 'идентификатор тестового задания';
comment on column task_options.option_text is 'текст варианта ответа';
comment on column task_options.is_correct is 'признак правильного варианта ответа';
comment on column task_options.sort_order is 'порядок отображения варианта ответа';

create table task_error_items (
    id              bigserial primary key,
    task_id         bigint not null references tasks(id) on delete cascade,
    fragment_text   text not null,
    is_error        boolean not null,
    explanation     text,
    sort_order      integer not null default 0,

    constraint chk_task_error_items_sort_order check (sort_order >= 0)
);

comment on table task_error_items is 'элементы задания на поиск ошибок';
comment on column task_error_items.id is 'уникальный идентификатор элемента';
comment on column task_error_items.task_id is 'идентификатор задания типа error_find';
comment on column task_error_items.fragment_text is 'текст фрагмента, который показывается пользователю';
comment on column task_error_items.is_error is 'признак того, что фрагмент содержит ошибку';
comment on column task_error_items.explanation is 'пояснение, почему фрагмент ошибочный или корректный';
comment on column task_error_items.sort_order is 'порядок отображения фрагмента';

create table attempts (
    id                  bigserial primary key,
    user_id             bigint not null references users(id) on delete cascade,
    task_id             bigint not null references tasks(id) on delete cascade,
    started_at          timestamp not null default now(),
    submitted_at        timestamp,
    status              attempt_status_enum not null default 'IN_PROGRESS',
    score               integer,
    max_score_snapshot  integer not null,
    is_correct          boolean,
    auto_checked        boolean not null default false,
    needs_manual_review boolean not null default false,
    reviewer_comment    text,
    reviewed_at         timestamp,

    constraint chk_attempts_score check (score is null or score >= 0),
    constraint chk_attempts_max_score_snapshot check (max_score_snapshot >= 0)
);

comment on table attempts is 'попытки выполнения заданий пользователями';
comment on column attempts.id is 'уникальный идентификатор попытки';
comment on column attempts.user_id is 'идентификатор пользователя';
comment on column attempts.task_id is 'идентификатор задания';
comment on column attempts.started_at is 'дата и время начала попытки';
comment on column attempts.submitted_at is 'дата и время отправки попытки на проверку';
comment on column attempts.status is 'статус попытки';
comment on column attempts.score is 'количество баллов, полученных за попытку';
comment on column attempts.max_score_snapshot is 'максимальный балл задания на момент прохождения';
comment on column attempts.is_correct is 'признак полностью корректного выполнения';
comment on column attempts.auto_checked is 'признак автоматической проверки попытки';
comment on column attempts.needs_manual_review is 'признак необходимости ручной проверки';
comment on column attempts.reviewer_comment is 'комментарий проверяющего';
comment on column attempts.reviewed_at is 'дата и время проверки попытки';

create table attempt_answers (
    id                      bigserial primary key,
    attempt_id              bigint not null references attempts(id) on delete cascade,
    answer_type             answer_type_enum not null,
    selected_option_id      bigint references task_options(id) on delete set null,
    selected_error_item_id  bigint references task_error_items(id) on delete set null,
    text_answer             text,

    constraint chk_attempt_answers_payload check (
        (
            answer_type = 'TEST_OPTION'
            and selected_option_id is not null
            and selected_error_item_id is null
            and text_answer is null
        )
        or
        (
            answer_type = 'ERROR_ITEM'
            and selected_error_item_id is not null
            and selected_option_id is null
            and text_answer is null
        )
        or
        (
            answer_type = 'OPEN_TEXT'
            and text_answer is not null
            and selected_option_id is null
            and selected_error_item_id is null
        )
    )
);

comment on table attempt_answers is 'конкретные ответы пользователя внутри попытки';
comment on column attempt_answers.id is 'уникальный идентификатор ответа';
comment on column attempt_answers.attempt_id is 'идентификатор попытки';
comment on column attempt_answers.answer_type is 'тип ответа: TEST_OPTION, ERROR_ITEM, OPEN_TEXT';
comment on column attempt_answers.selected_option_id is 'ссылка на выбранный вариант ответа для тестового задания';
comment on column attempt_answers.selected_error_item_id is 'ссылка на выбранный элемент в задании на поиск ошибок';
comment on column attempt_answers.text_answer is 'текстовый ответ для открытого задания';

create table user_progress (
    id                      bigserial primary key,
    user_id                 bigint not null references users(id) on delete cascade,
    simulator_id            bigint not null references simulators(id) on delete cascade,
    completed_tasks_count   integer not null default 0,
    total_tasks_count       integer not null default 0,
    total_score             integer not null default 0,
    completion_percent      numeric(5,2) not null default 0,
    last_activity_at        timestamp,

    unique (user_id, simulator_id),
    constraint chk_user_progress_completed_tasks_count check (completed_tasks_count >= 0),
    constraint chk_user_progress_total_tasks_count check (total_tasks_count >= 0),
    constraint chk_user_progress_total_score check (total_score >= 0),
    constraint chk_user_progress_completion_percent check (
        completion_percent >= 0 and completion_percent <= 100
    )
);

comment on table user_progress is 'агрегированный прогресс пользователя по тренажёру';
comment on column user_progress.id is 'уникальный идентификатор записи прогресса';
comment on column user_progress.user_id is 'идентификатор пользователя';
comment on column user_progress.simulator_id is 'идентификатор тренажёра';
comment on column user_progress.completed_tasks_count is 'количество завершённых заданий';
comment on column user_progress.total_tasks_count is 'общее количество заданий в тренажёре';
comment on column user_progress.total_score is 'суммарное количество набранных баллов';
comment on column user_progress.completion_percent is 'процент прохождения тренажёра';
comment on column user_progress.last_activity_at is 'дата и время последней активности пользователя в тренажёре';
