/**
 * Splits strings like "XmlHTTPRequest" or "NotExecuted" into "Xml HTTP Request" and "Not Executed".
 * @param text - the camelCase string to be split by whitespace.
 * @param capitalizeFirstLetter - if true, also capitalizes the first letter.
 */
export function splitCamelCase(
  text: string | undefined,
  capitalizeFirstLetter: boolean = false,
): string | undefined {
  const result =
    text !== undefined
      ? text
          .replace(/([a-z])([A-Z])/g, '$1 $2')
          .replace(/([A-Z]+)([A-Z][a-z])/g, '$1 $2')
      : undefined;

  return capitalizeFirstLetter ? capitalizeFirstCharacter(result) : result;
}

/**
 * Replaces every `\n` with &lt;br&gt; tag in a given string.
 * @param text - the string where you want `\n`'s to be replaced with.
 */
export function replaceLineBreaksWithHtmlTags(text: string | undefined) {
  return text?.split(/\n/).map((text) => (
    <>
      {text}
      <br />
    </>
  ));
}

/**
 * Converts `camelCase` string into `kebab-case` one.
 * @param input - the string to be converted into `kebab-case`.
 */
export function camelCaseToKebabCase(input: string | null | undefined) {
  return input?.replace(/[A-Z]/g, (match) => `-${match.toLowerCase()}`);
}

/**
 * Converts complex field names into camelCase and removes possible `$.` at the start.<br/>
 * <b>Example</b>: `GlobalParametersOptions[0].Options` becomes `globalParametersOptions[0].options`
 */
export function toCamelCasePath(input: string): string {
  const keyWithout$ = input.startsWith('$.') ? input.substring(2) : input;
  return keyWithout$.replace(/[A-Za-z_][A-Za-z0-9_]*/g, (segment) => {
    return segment.charAt(0).toLowerCase() + segment.slice(1);
  });
}

/**
 * Removes trailing chars if they exist in the string.
 * @param str
 * @param charsToRemove
 */
export function removeTrailingChars(str: string, charsToRemove: string) {
  return str.endsWith(charsToRemove)
    ? str.slice(0, -charsToRemove.length)
    : str;
}

/**
 * Appends trailing chars if they don't already exist in the string.
 * @param str
 * @param charsToAppend
 */
export function appendTrailingChars(str: string, charsToAppend: string) {
  return str.endsWith(charsToAppend) ? str : str + charsToAppend;
}

/**
 * Capitalizes the first character of the string.
 */
export function capitalizeFirstCharacter(
  input?: string | null | undefined,
): string | undefined;
export function capitalizeFirstCharacter(input: string): string;
export function capitalizeFirstCharacter(
  input?: string | null | undefined,
): string | undefined {
  return input ? input.charAt(0).toUpperCase() + input.slice(1) : undefined;
}

/**
 * Decapitalizes the first character of the string.
 */
export function decapitalizeFirstCharacter(
  input?: string | null | undefined,
): string | undefined;
export function decapitalizeFirstCharacter(input: string): string;
export function decapitalizeFirstCharacter(
  input?: string | null | undefined,
): string | undefined {
  return input ? input.charAt(0).toLowerCase() + input.slice(1) : undefined;
}
