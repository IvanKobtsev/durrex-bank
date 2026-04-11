import axios from "axios";
import {
  getUnknownErrorMessage,
  getUnknownErrorStack,
  markAsReported,
  sendRuntimeErrorToMonitoring,
  wasAlreadyReported,
} from "./monitoring/monitoring-service";

export const registerGlobalErrorHandlers = (): void => {
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
