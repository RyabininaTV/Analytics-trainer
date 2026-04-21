# 🚀 Analytics trainer

> Сервис для прохождения аналитических заданий и отслеживания прогресса пользователей

---

## 📌 Описание

Сервис представляет собой backend-приложение для платформы-тренажёра, в котором пользователи проходят аналитические 
задания, получают оценку результатов, баллы и отслеживают свой учебный прогресс

---

## 📘 Документация по процессам
- ### [Endpoints](docs/endpoints/index.md)

---

## 🛠️ Стек технологий
- ☕ Java 21 (язык разработки)
- ⚛️ Quarkus 3 (framework для разработки REST сервисов)
- 🐘 PostgreSQL (база данных)
- 🧩 jOOQ (конструктор SQL запросов)

---

## 🚀 Локальный запуск
1. Узнать значения `url`, `username` и `password` для подлючения к БД
2. Создать файл `application.yaml` в папке `config` в корне проекта (если такой папки нет, то создать) с содержимым ниже
   ```yaml
   quarkus:
     datasource:
     jdbc:
       url: <url>
       username: <username>
       password: <password>
   ```
3. Выполнить команду
   ```shell
   $env:QUARKUS_CONFIG_LOCATIONS="./config/application.yaml"
   .\gradlew.bat quarkusDev
   ```
