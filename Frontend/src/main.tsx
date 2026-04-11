import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { AuthProvider } from "react-oidc-context";
import { registerGlobalErrorHandlers } from "services/globalErrorHandlers.ts";

type ViteImportMeta = ImportMeta & {
  env?: Record<string, string | undefined>;
};

export const appEnv = (import.meta as ViteImportMeta).env ?? {};

export const userData: { userId: number | undefined } = { userId: undefined };

const oidcConfig = {
  authority: "https://swagor-time.ru/services/auth",
  client_id: "web-spa",
  redirect_uri: "https://swagor-time.ru/callback",
  post_logout_redirect_uri: "https://swagor-time.ru",
  scope: "openid profile bank_role bank.api offline_access",
  response_type: "code",
};

registerGlobalErrorHandlers();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </StrictMode>,
);
