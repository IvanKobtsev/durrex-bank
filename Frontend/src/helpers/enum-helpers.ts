import { capitalizeFirstCharacter } from './string-helpers';

/**
 * Casts Url parameter to the specified enum.
 * @param value The parameter value to cast.
 * @param enumType An enum to cast the passed string to.
 * @param defaultValue An optional default value.
 * @returns enum value if the cast is successful,
 * else the default value or the first value of the enum.
 */
export function castUrlParamToEnum<TEnum>(
  value: string | null | undefined,
  enumType: TEnum,
  defaultValue: TEnum[keyof TEnum],
): TEnum[keyof TEnum];
export function castUrlParamToEnum<TEnum>(
  value: string | null | undefined,
  enumType: TEnum,
): TEnum[keyof TEnum] | undefined;
export function castUrlParamToEnum<TEnum>(
  value: string | null | undefined,
  enumType: TEnum,
  defaultValue?: TEnum[keyof TEnum],
): TEnum[keyof TEnum] | undefined {
  const valueInPascalCase = capitalizeFirstCharacter(value);
  if (!valueInPascalCase) return defaultValue;

  return value &&
    Object.values(enumType as any).includes(valueInPascalCase as TEnum)
    ? (valueInPascalCase as TEnum[keyof TEnum])
    : defaultValue;
}
