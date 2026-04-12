import { QueryClient } from "@tanstack/react-query";
import axios from "axios";
import equal from "fast-deep-equal";
import { isCircuitBreakerOpen } from "services/axios/circuit-breaker.ts";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      structuralSharing: (oldData: any, newData) =>
        equal(oldData, newData) ? oldData : newData,
      refetchOnWindowFocus: false,
      throwOnError: true,
      retry(failureCount, error) {
        if (isCircuitBreakerOpen()) {
          return false;
        }

        if (failureCount >= 3) return false;

        if (axios.isAxiosError(error)) {
          if (error.response?.status === 401) {
            return false;
          }
        }

        return true;
      },
    },
  },
});
