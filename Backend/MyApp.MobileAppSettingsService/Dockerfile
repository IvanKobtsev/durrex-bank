FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY ["MyApp.MobileAppSettingsService/MyApp.MobileAppSettingsService.csproj", "MyApp.MobileAppSettingsService/"]
RUN dotnet restore "MyApp.MobileAppSettingsService/MyApp.MobileAppSettingsService.csproj"
COPY . .
WORKDIR "/src/MyApp.MobileAppSettingsService"
RUN dotnet build "MyApp.MobileAppSettingsService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "MyApp.MobileAppSettingsService.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MyApp.MobileAppSettingsService.dll"]
