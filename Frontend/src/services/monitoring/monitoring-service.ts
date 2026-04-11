import axios, { AxiosError } from "axios";
import { appEnv, userData } from "../../main.tsx";
import { traceId } from "services/axios/interceptors.ts";

const toMonitoringUserId = (value: number | undefined): string | undefined => {
  if (value === undefined || value === null) {
    return undefined;
  }

  return String(value);
};

const monitoringClient = axios.create({
  timeout: 5000,
});

const monitoringEventsUrl =
  appEnv.VITE_MONITORING_EVENTS_URL ??
  "https://swagor-time.ru/services/monitoring/api/events";
const reportedErrors = new WeakSet<object>();

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

export const wasAlreadyReported = (value: unknown): boolean => {
  if (
    (typeof value !== "object" && typeof value !== "function") ||
    value === null
  ) {
    return false;
  }

  return reportedErrors.has(value);
};

export const markAsReported = (value: unknown): void => {
  if (
    (typeof value !== "object" && typeof value !== "function") ||
    value === null
  ) {
    return;
  }

  reportedErrors.add(value);
};

export const sendMonitoringPayload = async (
  payload: Record<string, unknown>,
): Promise<void> => {
  try {
    await monitoringClient.post(monitoringEventsUrl, payload);
  } catch {
    // Swallow reporting errors to keep app flow unaffected.
  }
};

export const getUnknownErrorMessage = (value: unknown): string => {
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

export const getUnknownErrorStack = (value: unknown): string | undefined => {
  if (value instanceof Error) {
    return value.stack;
  }

  return undefined;
};

export const sendAxiosErrorToMonitoring = async (
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
  if (
    failedRequestPath &&
    monitoringPath &&
    failedRequestPath === monitoringPath
  ) {
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
    traceId: traceId,
    userId: toMonitoringUserId(userData.userId),
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

export const sendRuntimeErrorToMonitoring = async (
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
