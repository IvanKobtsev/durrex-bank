FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY ["MyApp.Gateway/MyApp.Gateway.csproj", "MyApp.Gateway/"]
RUN dotnet restore "MyApp.Gateway/MyApp.Gateway.csproj"
COPY MyApp.Gateway/ MyApp.Gateway/
WORKDIR "/src/MyApp.Gateway"
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "MyApp.Gateway.dll"]
