## Запускаем проект:

### локально:

1. Запускаем posgres
2. Заходим в папку `durrex-bank\Backend\MyApp.UserService`
3. Запускаем:
   ```powershell
   dotnet run
   ```
4. Swagger UI доступен по адресу [http://localhost:5004/swagger](http://localhost:5004/swagger)

### в докере:

1. Запускаем posgres
2. Заходим в папку `durrex-bank\Backend`
3. Запускаем:
   ```powershell
   docker run -d -p 8080:8080 `
   -v user-service-keys:/app/keys `
   -e ConnectionStrings__DefaultConnection="Host=host.docker.internal;Database=user_service;Username=postgres;Password=postgres" `
   -e AdminPassword="123123" `
   -e InternalApiKey="123123" `
   --name user-service `
   user-service
   ```
   Если у вас нестандартный пароль для юзера postgres, то меняйте его соответственно.
   Также обратите внимание не AdminPassword и InternalApkiey
4. Swagger UI доступен по адресу [http://localhost:8080/swagger/index.html](http://localhost:8080/swagger/index.html)
   Обратите внимание, что база данных создается на вашей машине и приложение, которое крутится в докер контейнере ходит в нее.
   В будущем все будет аркестровано общим docker-compose файлом, а пока так.

## Секреты:

- Если вы запускались через `dotnet run`, то секреты смотрите в appsettings.Development.json
- Если вы запускались через docker, то секреты, которые вы устанавливали флажками `-e` перетрут то, что прописано в appsettings.json, ну и appsettings.Development.json уже неактуален, потому что в контейнере крутится продовский билд, а не девовский.

## Флоу аутентификации:

### Старт Gateway

Gateway → UserService: GET /auth/public-key
Gateway сохраняет публичный ключ в памяти

### Запрос от клиента

Client → Gateway: POST /auth/login {username, password}
Gateway → UserService: POST /auth/login {username, password}
UserService: проверяет пароль через BCrypt
UserService: подписывает JWT приватным ключом (RS256)
UserService → Gateway: { token }
Gateway → Client: { token }

### Последующие запросы

Client → Gateway: GET /accounts + Authorization: Bearer <token>
Gateway: верифицирует подпись токена публичным ключом (локально, без UserService)
Gateway: извлекает userId + role, добавляет заголовки X-User-Id, X-User-Role
Gateway → CoreService: GET /accounts + X-User-Id: 42, X-User-Role: Client
