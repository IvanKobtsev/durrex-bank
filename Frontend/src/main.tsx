import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import axios, { AxiosError, AxiosResponse } from "axios";

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
    <App />
  </StrictMode>,
);
