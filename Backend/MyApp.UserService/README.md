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
  Client → Gateway: GET /accounts  +  Authorization: Bearer <token>
  Gateway: верифицирует подпись токена публичным ключом (локально, без UserService)
  Gateway: извлекает userId + role, добавляет заголовки X-User-Id, X-User-Role
  Gateway → CoreService: GET /accounts  +  X-User-Id: 42, X-User-Role: Client