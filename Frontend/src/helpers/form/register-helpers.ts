import {
  FieldPath,
  FieldValues,
  Path,
  RegisterOptions,
  UseFormReturn,
} from "react-hook-form";
import { numberField, requiredRule } from "./react-hook-form-helper";

export function registerDateTime<T extends FieldValues>(
  form: UseFormReturn<T>,
  name: Path<T>,
) {
  return {
    ...registerBase(form, name, {
      setValueAs: (v) => {
        return v ? new Date(v) : undefined;
      },
    }),
    type: "datetime-local",
  };
}

export function registerDate<T extends FieldValues>(
  form: UseFormReturn<T>,
  name: Path<T>,
) {
  return {
    ...registerBase(form, name, {
      valueAsDate: true,
      setValueAs: (v) => {
        return v ? v : undefined;
      },
    }),
    type: "date",
  };
}

export function registerNumber<T extends FieldValues>(
  form: UseFormReturn<T>,
  name: Path<T>,
  type: "int" | "currency" = "int",
) {
  return {
    ...registerBase(form, name, numberField()),
    type: "number",
    step: type === "int" ? "1" : "0.01",
  };
}

export function registerString<T extends FieldValues>(
  form: UseFormReturn<T>,
  name: Path<T>,
  options: { required?: boolean } = {},
) {
  const { required = false } = options;

  return {
    ...registerBase(form, name, required ? requiredRule() : {}),
    type: "text",
  };
}

export function registerPassword<T extends FieldValues>(
  form: UseFormReturn<T>,
  name: Path<T>,
) {
  return {
    ...registerBase(form, name, requiredRule()),
    type: "password",
  };
}

/**
 * A helper utility to
 * @param form
 * @param name
 * @param options
 */
export function registerBase<T extends FieldValues>(
  form: UseFormReturn<T>,
  name: Path<T>,
  options?: RegisterOptions<T, FieldPath<T>>,
) {
  return {
    ...form.register(name, options),
    errorText: extractError(form.formState.errors, name),
  };
}

export function extractError(errorsObj: any, name: string): string | undefined {
  if (name.includes(".")) {
    const currentKey = name.split(".")[0];
    const nextKeys = name.slice(name.indexOf(".") + 1);
    return extractError(errorsObj?.[currentKey], nextKeys);
  }
  return prettifyErrorMessage(errorsObj?.[name]?.message);
}

function prettifyErrorMessage(message: string | null) {
  return message?.split(";").join("\n");
}
