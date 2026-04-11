import axios, { AxiosError, AxiosResponse } from "axios";
import { sendAxiosErrorToMonitoring } from "services/monitoring/monitoring-service.ts";
import { generateTraceparent } from "helpers/traceparent.ts";

export const traceId = generateTraceparent();

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("access_token");

    config.headers = config.headers ?? {};
    config.headers["traceparent"] = traceId;

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
