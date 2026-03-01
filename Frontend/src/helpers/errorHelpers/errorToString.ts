import { convertToErrorStringInternal } from "./convertToErrorStringInternal.ts";
import { toCamelCasePath } from "../string-helpers.tsx";

/**
 * Converts exception object to readable string.
 * Handles ASP.NET Core validation errors (ProblemDetails), and other backend errors.
 * Returns 'Network Error' in case of network errors.
 * Returns 'Unauthorized' in case of 401
 * Returns 'Access Denied' in case of 403.
 * (errors are mentioned here for localization purposes)
 * @param error - can be:<br>
 *   - strongly-typed error (if server-side action is decorated with
 *   `[ProducesResponseType(400, Type = typeof(ValidationProblemDetails))]`)<br>
 *   - untyped error, in which case `error.response` will be populated with response in JSON.
 *   @param options
 */
export function errorToString(
  error: any,
  options?: { removePropertyNames?: boolean },
): string {
  const errorResponseData = error.response?.data || error.response || error;
  const errors = errorResponseData?.errors;

  let overallError = convertToErrorStringInternal(error);

  if (errors && Object.keys(errors).length) {
    let formErrorsCombined = "";

    Object.entries(errors).forEach((pair) => {
      const { keyInCamelCase, errorText } = textFromErrorKeyValue(pair);

      if (options?.removePropertyNames) {
        formErrorsCombined = formErrorsCombined + `${errorText}\n`;
      } else {
        formErrorsCombined =
          formErrorsCombined + `${keyInCamelCase}: ${errorText}\n`;
      }
    });

    if (
      overallError &&
      overallError !== "One or more validation errors occurred."
    ) {
      overallError = overallError + "\n" + formErrorsCombined;
    } else {
      overallError = formErrorsCombined;
    }
  }

  return overallError.trim();
}

export function textFromErrorKeyValue([key, value]: [string, unknown]) {
  console.log("textFromErrorKeyValue", key, value);
  const keyInCamelCase = toCamelCasePath(key);
  const errorText = Array.isArray(value) ? value.join("; ") : (value as string);

  return {
    keyInCamelCase,
    errorText,
  };
}
