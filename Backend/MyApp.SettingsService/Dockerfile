FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY ["MyApp.SettingsService/MyApp.SettingsService.csproj", "MyApp.SettingsService/"]
RUN dotnet restore "MyApp.SettingsService/MyApp.SettingsService.csproj"
COPY . .
WORKDIR "/src/MyApp.SettingsService"
RUN dotnet build "MyApp.SettingsService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "MyApp.SettingsService.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MyApp.SettingsService.dll"]
