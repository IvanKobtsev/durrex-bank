FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY ["MyApp.CreditService/MyApp.CreditService.csproj", "MyApp.CreditService/"]
RUN dotnet restore "MyApp.CreditService/MyApp.CreditService.csproj"
COPY MyApp.CreditService/ MyApp.CreditService/
WORKDIR "/src/MyApp.CreditService"
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "MyApp.CreditService.dll"]
