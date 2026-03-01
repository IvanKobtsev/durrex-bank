import i18next from "i18next";
import {
  ApiException,
  ProblemDetails,
} from "../../services/core-api/core-api-client.types.ts";

export function convertToErrorStringInternal(error: any): string {
  const errorResponseData = error.response?.data || error.response || error;
  const responseDetail = errorResponseData?.detail;

  if (error.status === 401) {
    return i18next.t("Errors.Unauthorized");
  }
  if (error.status === 403) {
    return i18next.t("Errors.AccessDenied");
  }

  if (ApiException.isApiException(error)) {
    error = error.response as any as ProblemDetails;
  }

  const internalError = checkIfInternalError(error);
  if (internalError) return internalError;

  if (responseDetail) {
    // General server-side error not related to certain field (e.g. `Access Denied`)
    return responseDetail;
  }

  if (error.message) {
    // e.g. Network Error
    console.log("Error:", error, JSON.stringify(error));
    return error.message;
  } else if (error.title) {
    return error.title;
  } else if (typeof error === "string") {
    return error;
  }
  console.log("Unknown Error:", error, JSON.stringify(error));
  return error.toString();
}

function checkIfInternalError(error: any): string | null {
  let result = null;

  Object.entries(InternalErrorsChecks).forEach(
    ([internalError, errorChecks]) => {
      if (errorChecks.some((check) => check(error))) {
        result = internalError;
      }
    },
  );

  return result;
}

export enum InternalErrorType {
  NetworkError = "NetworkError",
  PageNotFound = "PageNotFound",
  AccessDenied = "AccessDenied",
}

/**
 * Just a nice way of binding different checks to internal errors.
 * Note that the order matters, and if the upper-declared type of error has the
 * same check with any error below it, checkIfInternalError() will return
 * the first one.
 */
const InternalErrorsChecks: Record<
  InternalErrorType,
  ((error: any) => boolean)[]
> = {
  NetworkError: [
    (error) => error.code === "CSS_CHUNK_LOAD_FAILED",
    (error) => error.name === "ChunkLoadError",
    (error) => error.message?.includes("fetch dynamically imported module"),
    (error) =>
      error.message?.includes("Cannot read property 'status' of undefined"),
    (error) => error.message?.includes("Cannot read properties of undefined"),
  ],
  PageNotFound: [
    (error) =>
      // Is thrown by ReactRouter when it encounters the value in route parameter of invalid type
      error.message?.includes("Unable to convert"),
  ],
  AccessDenied: [
    (error) =>
      error.error_description?.includes(
        "The mandatory 'refresh_token' parameter is missing.",
      ),
  ],
} as const;
