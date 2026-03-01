import { ApiException } from "services/core-api/core-api-client.types.ts";

export function isApiException(
  error: unknown | ApiException | null | undefined,
): error is ApiException {
  return !!error && !!(error as ApiException).status;
}

export function isNotFound(error: unknown | ApiException | null | undefined) {
  return !!error && (error as ApiException).status === 404;
}

export function isClientError(
  error: unknown | ApiException | null | undefined,
) {
  return (
    !!error &&
    (error as ApiException).status >= 400 &&
    (error as ApiException).status < 500
  );
}
