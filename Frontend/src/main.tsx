import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import axios, { AxiosError, AxiosResponse } from "axios";
import { AuthProvider } from "react-oidc-context";

const oidcConfig = {
  authority: "http://localhost:5260/auth", // ← Gateway, not direct AuthService
  client_id: "web-spa",
  redirect_uri: "http://localhost:5173/callback",
  post_logout_redirect_uri: "http://localhost:5173",
  scope: "openid profile bank_role bank.api offline_access",
  response_type: "code",
};

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("access_token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

axios.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error: AxiosError<any>) => {
    if (
      error.response &&
      error.response.status === 401 &&
      error.response?.data === "Invalid or expired token."
    ) {
      localStorage.removeItem("access_token");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  },
);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </StrictMode>,
);
