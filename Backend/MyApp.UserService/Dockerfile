FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY ["MyApp.UserService/MyApp.UserService.csproj", "MyApp.UserService/"]
RUN dotnet restore "MyApp.UserService/MyApp.UserService.csproj"
COPY MyApp.UserService/ MyApp.UserService/
WORKDIR "/src/MyApp.UserService"
RUN dotnet publish -c Release -o /app/publish --no-restore

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
VOLUME /app/keys
ENTRYPOINT ["dotnet", "MyApp.UserService.dll"]
