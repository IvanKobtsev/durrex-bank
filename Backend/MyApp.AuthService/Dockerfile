FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src
COPY ["MyApp.AuthService/MyApp.AuthService.csproj", "MyApp.AuthService/"]
RUN dotnet restore "MyApp.AuthService/MyApp.AuthService.csproj"
COPY . .
WORKDIR "/src/MyApp.AuthService"
RUN dotnet build "MyApp.AuthService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "MyApp.AuthService.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MyApp.AuthService.dll"]
