## Запускаем проект:

### локально

1. Заходим в папку `durrex-bank\Backend\MyApp.CreditService`
2. Запускаем:
   ```powershell
   dotnet run
   ```
3. Swagger UI доступен по адресу [http://localhost:5115/swagger](http://localhost:5115/swagger)

### в докере:

#### Build (в папке /Backend)

```powershell
docker build -f MyApp.CreditService/Dockerfile -t credit-service .
```

#### Run (в папке /Backend)

```powershell
docker run -p 5002:8080 `
  -e "ConnectionStrings__Default=Host=host.docker.internal;Database=credit_db;Username=postgres;Password=postgres" `
  -e "Services__CoreService__BaseUrl=http://host.docker.internal:5001" `
  -e "InternalApiKey=dev-internal-key-12345" `
  credit-service
```

Swagger UI доступен по адресу [http://localhost:5002/swagger/index.html](http://localhost:5002/swagger/index.html)
