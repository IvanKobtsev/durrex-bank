# Source Maps

Frontend build artifacts are copied into this directory by the Vite plugin in `Frontend/vite.config.mts`.

Expected structure:

- `SourceMaps/frontend-web/assets/*.js.map`

`MyApp.MonitoringService` resolves minified JavaScript stack trace frames against these files before storing events.

