FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY ["MyApp.WebAppSettingsService/MyApp.WebAppSettingsService.csproj", "MyApp.WebAppSettingsService/"]
RUN dotnet restore "MyApp.WebAppSettingsService/MyApp.WebAppSettingsService.csproj"
COPY . .
WORKDIR "/src/MyApp.WebAppSettingsService"
RUN dotnet build "MyApp.WebAppSettingsService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "MyApp.WebAppSettingsService.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MyApp.WebAppSettingsService.dll"]
