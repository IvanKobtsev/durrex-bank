import axios, { AxiosError, AxiosResponse } from "axios";
import { sendAxiosErrorToMonitoring } from "services/monitoring/monitoring-service.ts";
import { generateTraceparent } from "helpers/traceparent.ts";
import {
  CircuitBreakerOpenError,
  getCircuitBreakerRemainingMs,
  isCircuitBreakerOpen,
  recordRequestOutcome,
} from "services/axios/circuit-breaker.ts";

export const traceId = generateTraceparent();

const isRefusedConnectionError = (error: AxiosError<any>): boolean => {
  if (error.response?.status === 503) {
    return true;
  }

  if (error.response) {
    return false;
  }

  const code = error.code?.toLowerCase();
  const message = error.message?.toLowerCase() ?? "";
  return (
    code === "err_network" ||
    code === "econnrefused" ||
    code === "err_connection_refused" ||
    message.includes("connection refused") ||
    message.includes("refused connection")
  );
};

axios.interceptors.request.use(
  (config) => {
    if (isCircuitBreakerOpen()) {
      const remainingMs = getCircuitBreakerRemainingMs();
      return Promise.reject(
        new CircuitBreakerOpenError(
          `Circuit breaker is open for ${remainingMs}ms`,
        ),
      );
    }

    const token = localStorage.getItem("access_token");

    config.headers = config.headers ?? {};
    config.headers["traceparent"] = traceId;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    const mutatingMethods = ["POST", "PUT", "PATCH", "DELETE"];
    if (mutatingMethods.includes((config.method ?? "").toUpperCase())) {
      config.headers["Idempotency-Key"] = crypto.randomUUID();
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

axios.interceptors.response.use(
  (response: AxiosResponse) => {
    recordRequestOutcome(false);
    return response;
  },
  (error: AxiosError<any>) => {
    if (error instanceof CircuitBreakerOpenError) {
      return Promise.reject(error);
    }

    if (error.code !== "ERR_CANCELED") {
      recordRequestOutcome(isRefusedConnectionError(error));
    }

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
