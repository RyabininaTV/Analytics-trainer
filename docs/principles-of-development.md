# 🧭 Принципы разработки
> Короткая памятка для команды. Цель — писать ручки одинаково, чтобы код легко ревьюить, тестировать и дорабатывать 

---

## 🏗️ Базовая идея — разделение на слои
> Каждый слой делает только свою работу и не лезет в ответственность другого слоя 
- 🌐 `Controller` — HTTP-слой: path, method, `@Valid`, `@Secured`, вызов сервиса, возврат `Response`
- ⚙️ `Service` — бизнес-сценарий одной ручки: проверки, вызовы репозиториев, сборка ответа
- 🗄️ `Repository` — только SQL/jOOQ-запросы к одной таблице. Бизнес-правила сюда не кладём 
- 🧱 `Entity request/response` — объекты между service и repository. jOOQ Record наружу из repository не отдаём 
- 📦 `DTO request/response` — внешний контракт API. Именно они видны клиенту и OpenAPI

---

## 🛠️ Последовательность написания новой ручки 
1. Определить `endpoint` и добавить константу в `*Endpoints`
2. Создать `request DTO`, если ручка принимает body
3. Создать `response DTO`, если ручка возвращает body
4. Создать отдельный `service` под эту ручку
5. Создать нужные `repository request/response entity`
6. Добавить query-метод в нужный `repository`. 
7. Добавить метод в `Controller`: он только вызывает service и возвращает `Response`
8. Добавить `custom exceptions` для бизнес-ошибок
9. Написать unit-тест сервиса
10. Проверить ручку через `Swagger/Postman`

---

## 🌐 Controller
> Контроллер должен быть максимально тонким. В нём не должно быть бизнес-логики, парсинга токенов, проверок 
существования сущностей, маппинга сложных объектов и SQL
- Всегда возвращаем `jakarta.ws.rs.core.Response` 
- `DI` делаем через `final-поля` и `Lombok`
- `Endpoint path` берём из констант, а не пишем строкой прямо в `@Path `
- Для защищённых ручек ставим `@Secured` с явными ролями
- Для body-параметров ставим `@Valid`
```java
@Path(BASE) // базовый префикс
@RequiredArgsConstructor // генерирует конструктор класса для всех final полей 
@Consumes(APPLICATION_JSON) // задает тип принимаемых данных как JSON
@Produces(APPLICATION_JSON) // задает тип возвращаемых данных как JSON
@FieldDefaults(level = PRIVATE, makeFinal = true) // генерирует для всех не static полей приставку private final
public class TestController {

    TestService testService; // инжект сервиса
    
    @POST // тип запроса POST
    @Path(TEST_ENDPOINT) // endpoint ручки в итоге суммируется с префиксом контроллера
    @Secured({USER, ADMIN}) // защита ручки, чтобы она была доступна только авторизованным пользователям
    public Response testEndpoint(@Valid TestRequest request) { // Valid -- валидирует реквест
        return Response.ok(testService.test(request)).build(); // строим ответ ручки
    }

}
```

---

## ⚙️ Service
> Одна ручка — один service — один публичный бизнес-метод. Так проще понимать сценарий и проще писать unit-тесты
- В сервисе живёт бизнес-логика конкретной ручки
- Проверки пишем декларативно через `Checker`, если это уже принятый стиль проекта
- `DTO/response` собираем через `builder`, не через длинные конструкторы
- Приватные `helper-методы` делаем `static`, если они не используют поля класса
- Нормализацию `email` делаем через `EmailUtil.normalize(...)`
- Не создаём `service` ради технической обёртки, если это просто `util-логика` без состояния 
```java
@ApplicationScoped // позволяет инжектить сервис в контроллер
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SomeUseCaseService {
    
    SomeRepository repository; //инжект репозитория

    public SomeResponse execute(@Nonnull SomeRequest request) {         
        // бизнес-сценарий ручки     
    }
    
}
```

---

## 🗄️ Repository
> Репозиторий привязан к таблице, а не к ручке. У одной таблицы может быть много query-методов
- Одна таблица — один `repository`
- `Repository` не содержит бизнес-логики. Он только читает/пишет данные
- Если метод принимает больше одного параметра — создаём `request entity`
- Если метод возвращает набор полей — создаём `response entity`
- `jOOQ Record` не возвращаем из `repository`
- Маппинг результата держим рядом с запросом, а не плодим `private mapper-методы` на каждый `query`
- В `select` выбираем только нужные поля, а не всю таблицу без причины
```java
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserRepository {
    
    DSLContext dsl;
    
    public Optional<FindUserByIdResponseEntity> findById(@Nonnull Long id) {
        return dsl.select(
                    USERS.ID, 
                    USERS.EMAIL, 
                    USERS.USERNAME
                )
                .from(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptional(record -> FindUserByIdResponseEntity.builder()
                        .id(record.get(USERS.ID))
                        .email(record.get(USERS.EMAIL))
                        .username(record.get(USERS.USERNAME))
                        .build()
                );
    }
    
}
```

---

## 🧱 Entity между service и repository
> Entity request/response — это внутренний контракт между сервисом и репозиторием. Он не равен DTO и не равен jOOQ 
Record
- `Request entity` называем по действию: `CreateUserRequestEntity`, `UpdateUserPasswordRequestEntity`
- `Response entity` называем по запросу: `FindUserByEmailResponseEntity`, `CreateUserResponseEntity`
- Для record-ов, которые создаём руками в коде, используем `@Builder`
- На поля ставим `@Nonnull` или `@Nullable` осознанно
- Если поле не нужно сервису — не добавляем его в `entity`

### Правило для nullable
- Поле выбрано из БД и в БД `NOT NULL` — ставим `@Nonnull`
- Поле выбрано из БД и в БД `nullable` — ставим `@Nullable`
- Поле не выбрано в запросе — его не должно быть в `response entity`

---

## 📦 DTO request/response
> DTO — это внешний контракт API. Его увидят frontend, Postman, Swagger/OpenAPI и другие клиенты
- `Request DTO` валидируем через `Bean Validation`: `@NotBlank`, `@Email`, `@Size` и т.д. 
- `Response DTO` собираем через `builder`
- Для JSON-имён используем `snake_case` через `@JsonProperty`
- Для `response DTO` можно ставить `@JsonInclude(NON_NULL)`, чтобы null-поля не попадали в ответ
- Для OpenAPI позже добавляем минимальные `@Schema` и `@APIResponse`, без перегруза аннотациями

---

## 🛡️ Security
> Бизнес-ручки не должны вручную парсить JWT. За это отвечает JwtAuthFilter 
- Обычная защищённая ручка: `@Secured(roles = {UserRoleEnum.USER, UserRoleEnum.ADMIN})` 
- Админская ручка: `@Secured(roles = {UserRoleEnum.ADMIN})`
- Если сервису нужен текущий пользователь — инжектим `CurrentUserContext` в `service` и вызываем `require()` 
- Не достаём пользователя из токена руками в каждой ручке
```java
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetProfileService {
    
    CurrentUserContext currentUserContext;

    public ProfileResponse getProfile() {
        CurrentUser currentUser = currentUserContext.require(); //достаем текущего пользователя
        return ProfileResponse.builder()
                .userId(currentUser.id())
                .email(currentUser.email())
                .role(currentUser.role())
                .build();
    }
    
}
```

---

## ⚠️ Исключения
> Для бизнес-ошибок создаём отдельные exception-классы. Не кидаем везде голый WebApplicationException
- `InvalidEmailOrPasswordException` — неверный email или пароль
- `UserIsBlockedException` — пользователь заблокирован
- `EmailIsAlreadyUsedException` — email уже занят
- `UsernameIsAlreadyUsedException` — username уже занят
- `InvalidRefreshTokenException` — refresh token невалиден
- `RefreshTokenIsRevokedException` — refresh token отозван

---

## 🧪 Unit-тесты
> Unit-тесты пишем на service. Репозитории мокируем. Утилиты типа BcryptUtil/JwtUtil можно использовать настоящие, если 
это упрощает тест и не требует внешней инфраструктуры
- Тестируем успешный сценарий 
- Тестируем основные бизнес-ошибки
- Проверяем, какие методы repository были вызваны
- Если сервис создаёт `request entity` для `repository` — ловим `ArgumentCaptor` и проверяем поля
