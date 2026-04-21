-- trainers
INSERT INTO trainers  (name, description, difficulty, total_tasks, total_max_score, time_limit_seconds, sort_order, is_active)
VALUES ('тестовые задания', 'Для начинающих', 'E', 3, 100, 600, 1, TRUE);

INSERT INTO trainers  (name, description, difficulty, total_tasks, total_max_score, time_limit_seconds, sort_order, is_active)
VALUES ('задания на поиск ошибок', 'Средний уровень', 'N', 3, 1800, 600, 2, TRUE);

INSERT INTO trainers  (name, description, difficulty, total_tasks, total_max_score, time_limit_seconds, sort_order, is_active)
VALUES ('открытые задания', 'Сложные задачи', 'H', 3, 100, 3600, 3, TRUE);

-- tasks
-- type: 1-вопрос с 1 ответом, 2-вопрос с несколькими ответами, 3-текстовое поле для ответа
INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='тестовые задания'),
    'Вопрос 1',
    'Какой порт используется PostgreSQL по умолчанию?',
    '1',
    30,
    '{"title":"тестовые задания","difficulty":"E","questions":[{"id":1,"question":"Какой порт используется PostgreSQL по умолчанию?","options":["3306","1433","5432","27017"]}]}',
    '{"title":"тестовые задания","difficulty":"E","questions":[{"id":1,"question":"Какой порт используется PostgreSQL по умолчанию?","correct":"5432"}]}',
    1
);

INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='тестовые задания'),
    'Вопрос 2',
    'Какая команда используется для выбора всех записей из таблицы ''users''?',
    '1',
    30,
    '{"title":"тестовые задания","difficulty":"E","questions":[{"id":2,"question":"Какая команда используется для выбора всех записей из таблицы ''users''?","options":["SELECT ALL FROM users","GET * FROM users","SELECT * FROM users","SHOW users"]}]}',
    '{"title":"тестовые задания","difficulty":"E","questions":[{"id":2,"question":"Какая команда используется для выбора всех записей из таблицы ''users''?","correct":"SELECT * FROM users"}]}',
    2
);

INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='тестовые задания'),
    'Вопрос 3',
    'Какой тип данных в PostgreSQL используется для хранения целых чисел?',
    '1',
    40,
    '{"title":"тестовые задания","difficulty":"E","questions":[{"id":3,"question":"Какой тип данных в PostgreSQL используется для хранения целых чисел?","options":["VARCHAR","INTEGER","TEXT","BOOLEAN"]}]}',
    '{"title":"тестовые задания","difficulty":"E","questions":[{"id":3,"question":"Какой тип данных в PostgreSQL используется для хранения целых чисел?","correct":"INTEGER"}]}',
    3
);

-- 2
INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='задания на поиск ошибок'),
    'Вопрос 1',
    'Какие характеристики должны быть у хорошего требования?',
    '2',
    30,
    '{"title":"задания на поиск ошибок","difficulty":"N","questions":[{"id":4,"question":"Какие характеристики должны быть у хорошего требования?","options":["Однозначность","Противоречивость","Проверяемость","Избыточность"]}]}',
    '{"title":"задания на поиск ошибок","difficulty":"N","questions":[{"id":4,"question":"Какие характеристики должны быть у хорошего требования?","correct":["Однозначность","Проверяемость"]}]}',
    1
);

INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='задания на поиск ошибок'),
    'Вопрос 2',
    'Найдите ошибки в пользовательской истории: ''Как пользователь, я хочу удалять всё, чтобы было чисто''.',
    '2',
    30,
    '{"title":"задания на поиск ошибок","difficulty":"N","questions":[{"id":5,"question":"Найдите ошибки в пользовательской истории: ''Как пользователь, я хочу удалять всё, чтобы было чисто''.","options":["Отсутствует бизнес-ценность","Нет критериев приемки","Слишком много технических терминов","Не определена роль (пользователь — слишком общо)"]}]}',
    '{"title":"задания на поиск ошибок","difficulty":"N","questions":[{"id":5,"question":"Найдите ошибки в пользовательской истории: ''Как пользователь, я хочу удалять всё, чтобы было чисто''.","correct":["Отсутствует бизнес-ценность","Нет критериев приемки","Не определена роль (пользователь — слишком общо)"]}]}',
    2
);

INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='задания на поиск ошибок'),
    'Вопрос 3',
    'Какие методы сбора требований существуют?',
    '2',
    40,
    '{"title":"задания на поиск ошибок","difficulty":"N","questions":[{"id":6,"question":"Какие методы сбора требований существуют?","options":["Интервью","Наблюдение","Компиляция кода","Анализ существующих документов"]}]}',
    '{"title":"задания на поиск ошибок","difficulty":"N","questions":[{"id":6,"question":"Какие методы сбора требований существуют?","correct":["Интервью","Наблюдение","Анализ существующих документов"]}]}',
    3
);

-- 3
INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='открытые задания'),
    'Вопрос 1',
    'Спроектируйте структуру документа ''Требования к интеграции'' для системы, которая обменивается данными с тремя внешними сервисами (CRM, ERP, платежный шлюз). Укажите минимум 4 обязательных раздела.',
    '3',
    30,
    '{"title":"открытые задания","difficulty":"H","questions":[{"id":7,"question":"Спроектируйте структуру документа ''Требования к интеграции'' для системы, которая обменивается данными с тремя внешними сервисами (CRM, ERP, платежный шлюз). Укажите минимум 4 обязательных раздела.","options":[]}]}',
    '{"title":"открытые задания","difficulty":"H","questions":[{"id":7,"question":"Спроектируйте структуру документа ''Требования к интеграции'' для системы, которая обменивается данными с тремя внешними сервисами (CRM, ERP, платежный шлюз). Укажите минимум 4 обязательных раздела.","correct":["Форматы данных (JSON/XML)","Протоколы (REST/SOAP/gRPC)","Сценарии ошибок и повторные попытки","Аутентификация и авторизация"]}]}',
    1
);

INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='открытые задания'),
    'Вопрос 2',
    'К вам пришёл разработчик с вопросом: ''В требованиях написано поле Имя — обязательное, а в прототипе — нет звёздочки. Что делать?'' Опишите ваш алгоритм действий как middle-аналитика (по шагам).',
    '3',
    30,
    '{"title":"открытые задания","difficulty":"H","questions":[{"id":8,"question":"К вам пришёл разработчик с вопросом: ''В требованиях написано поле Имя — обязательное, а в прототипе — нет звёздочки. Что делать?'' Опишите ваш алгоритм действий как middle-аналитика (по шагам).","options":[]}]}',
    '{"title":"открытые задания","difficulty":"H","questions":[{"id":8,"question":"К вам пришёл разработчик с вопросом: ''В требованиях написано поле Имя — обязательное, а в прототипе — нет звёздочки. Что делать?'' Опишите ваш алгоритм действий как middle-аналитика (по шагам).","correct":["Форматы данных (JSON/XML)","Протоколы (REST/SOAP/gRPC)","Сценарии ошибок и повторные попытки","Аутентификация и авторизация"]}]}',
    2
);

INSERT INTO tasks (
    trainer_id,
    title,
    description,
    type,
    score,
    task_data,
    correct_answer,
    sort_order
) VALUES (
    (select id from trainers where name='открытые задания'),
    'Вопрос 3',
    'Что такое ''инцидент анализа'' (analysis incident) и как его обрабатывать?',
    '3',
    40,
    '{"title":"открытые задания","difficulty":"H","questions":[{"id":9,"question":"Что такое ''инцидент анализа'' (analysis incident) и как его обрабатывать?","options":[]}]}',
    '{"title":"открытые задания","difficulty":"H","questions":[{"id":9,"question":"Что такое ''инцидент анализа'' (analysis incident) и как его обрабатывать?","correct":["Обнаруженное противоречие или пробел в требованиях, фиксируется и выносится на согласование"]}]}',
    3
);