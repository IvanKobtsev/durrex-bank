import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import axios, { AxiosError, AxiosResponse } from "axios";
import { AuthProvider } from "react-oidc-context";

type ViteImportMeta = ImportMeta & {
  env?: Record<string, string | undefined>;
};

const appEnv = (import.meta as ViteImportMeta).env ?? {};

const monitoringClient = axios.create({
  timeout: 5000,
});

const monitoringEventsUrl =
  appEnv.VITE_MONITORING_EVENTS_URL ??
  "https://swagor-time.ru/services/monitoring/api/events";
const reportedErrors = new WeakSet<object>();

const oidcConfig = {
  authority: "https://swagor-time.ru/services/auth",
  client_id: "web-spa",
  redirect_uri: "https://swagor-time.ru/callback",
  post_logout_redirect_uri: "https://swagor-time.ru",
  scope: "openid profile bank_role bank.api offline_access",
  response_type: "code",
};

const getRequestPath = (url?: string, baseURL?: string): string | undefined => {
  if (!url) {
    return undefined;
  }

  try {
    return new URL(url, baseURL ?? window.location.origin).pathname;
  } catch {
    return url;
  }
};

const getErrorMessage = (error: AxiosError<unknown>): string => {
  const responseData = error.response?.data;

  if (typeof responseData === "string" && responseData.trim().length > 0) {
    return responseData;
  }

  if (responseData && typeof responseData === "object") {
    const message = (responseData as { message?: unknown }).message;
    if (typeof message === "string" && message.trim().length > 0) {
      return message;
    }
  }

  return error.message || "Unknown frontend axios error";
};

const wasAlreadyReported = (value: unknown): boolean => {
  if ((typeof value !== "object" && typeof value !== "function") || value === null) {
    return false;
  }

  return reportedErrors.has(value);
};

const markAsReported = (value: unknown): void => {
  if ((typeof value !== "object" && typeof value !== "function") || value === null) {
    return;
  }

  reportedErrors.add(value);
};

const sendMonitoringPayload = async (
  payload: Record<string, unknown>,
): Promise<void> => {
  try {
    await monitoringClient.post(monitoringEventsUrl, payload);
  } catch {
    // Swallow reporting errors to keep app flow unaffected.
  }
};

const getUnknownErrorMessage = (value: unknown): string => {
  if (value instanceof Error) {
    return value.message || value.name;
  }

  if (typeof value === "string" && value.trim().length > 0) {
    return value;
  }

  if (value === undefined) {
    return "Unhandled promise rejection without reason";
  }

  try {
    return JSON.stringify(value);
  } catch {
    return String(value);
  }
};

const getUnknownErrorStack = (value: unknown): string | undefined => {
  if (value instanceof Error) {
    return value.stack;
  }

  return undefined;
};

const sendAxiosErrorToMonitoring = async (
  error: AxiosError<unknown>,
): Promise<void> => {
  if (wasAlreadyReported(error)) {
    return;
  }

  markAsReported(error);

  const failedRequestPath = getRequestPath(
    error.config?.url,
    error.config?.baseURL,
  );
  const monitoringPath = getRequestPath(monitoringEventsUrl);

  // Avoid reporting failures produced by monitoring ingestion itself.
  if (failedRequestPath && monitoringPath && failedRequestPath === monitoringPath) {
    return;
  }

  await sendMonitoringPayload({
    service: "frontend-web",
    environment: appEnv.MODE ?? "unknown",
    level: "error",
    message: getErrorMessage(error),
    exceptionType: "AxiosError",
    stackTrace: error.stack,
    requestMethod: error.config?.method?.toUpperCase(),
    requestPath: failedRequestPath,
    traceId:
      (error.response?.headers?.["x-trace-id"] as string | undefined) ??
      (error.response?.headers?.["trace-id"] as string | undefined),
    occurredAtUtc: new Date().toISOString(),
    tags: {
      source: "frontend",
      type: "axios-response-error",
    },
    additionalData: {
      code: error.code,
      statusCode: error.response?.status,
      requestUrl: error.config?.url,
    },
  });
};

const sendRuntimeErrorToMonitoring = async (
  message: string,
  exceptionType: string,
  stackTrace?: string,
  additionalData?: Record<string, unknown>,
): Promise<void> => {
  await sendMonitoringPayload({
    service: "frontend-web",
    environment: appEnv.MODE ?? "unknown",
    level: "error",
    message,
    exceptionType,
    stackTrace,
    requestPath: window.location.pathname,
    occurredAtUtc: new Date().toISOString(),
    tags: {
      source: "frontend",
      type: "runtime-error",
    },
    additionalData,
  });
};

const registerGlobalErrorHandlers = (): void => {
  window.addEventListener("error", (event) => {
    if (event.error && wasAlreadyReported(event.error)) {
      return;
    }

    markAsReported(event.error);
    void sendRuntimeErrorToMonitoring(
      event.message || "Unhandled browser runtime error",
      event.error?.name || "WindowErrorEvent",
      event.error?.stack,
      {
        filename: event.filename,
        lineNumber: event.lineno,
        columnNumber: event.colno,
      },
    );
  });

  window.addEventListener("unhandledrejection", (event) => {
    const reason = event.reason;

    // Axios response errors are already captured by the Axios interceptor.
    if (axios.isAxiosError(reason)) {
      return;
    }

    if (wasAlreadyReported(reason)) {
      return;
    }

    markAsReported(reason);
    void sendRuntimeErrorToMonitoring(
      getUnknownErrorMessage(reason),
      reason instanceof Error ? reason.name : "UnhandledPromiseRejection",
      getUnknownErrorStack(reason),
      {
        rejectionType: typeof reason,
      },
    );
  });
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
    void sendAxiosErrorToMonitoring(error);

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

registerGlobalErrorHandlers();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </StrictMode>,
);
