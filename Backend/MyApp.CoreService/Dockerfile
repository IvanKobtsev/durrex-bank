FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src
COPY ["MyApp.CoreService/MyApp.CoreService.csproj", "MyApp.CoreService/"]
RUN dotnet restore "MyApp.CoreService/MyApp.CoreService.csproj"
COPY MyApp.CoreService/ MyApp.CoreService/
WORKDIR "/src/MyApp.CoreService"
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "MyApp.CoreService.dll"]
