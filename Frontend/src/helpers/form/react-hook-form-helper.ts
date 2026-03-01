import i18n from 'i18next';
import { useEffect } from 'react';
import {
  DefaultValues,
  FieldValues,
  RegisterOptions,
  UseFormReturn,
} from 'react-hook-form';
import superjson from 'superjson';

/*
This hook is useful, when defaultValues of the Form become available AFTER form is initially rendered.
E.g. when defaultValues are loaded via react-query.
When defaultValues change, form fields are updated according to changed values.
 */
export function useResetFormWhenDataIsLoaded<
  TFieldValues extends FieldValues = FieldValues,
  TContext extends object = object,
>(
  form: UseFormReturn<TFieldValues, TContext>,
  defaultValues?: DefaultValues<TFieldValues>,
) {
  useEffect(() => {
    if (defaultValues) {
      // { ...defaultValues } is required to not modify original defaultValues
      const clonedValues = superjson.parse<DefaultValues<TFieldValues>>(
        superjson.stringify(defaultValues),
      );
      form.reset(clonedValues, {
        keepDirtyValues: true,
      });
    }
  }, [superjson.stringify(defaultValues)]);
}

export function requiredRule(allowJustSpaces: boolean = false) {
  return {
    required: {
      value: true,
      message: i18n.t('UIKit.Inputs.Required'),
    },
    validate: allowJustSpaces
      ? () => undefined
      : (value: any) =>
          typeof value === 'string'
            ? value.trim().length > 0 || i18n.t('UIKit.Inputs.SpacesNotAllowed')
            : undefined,
  };
}

export function maxLengthRule(maxLength: number) {
  return {
    maxLength: {
      value: maxLength,
      message: i18n.t('UIKit.Inputs.MaxLength', {
        number: maxLength,
      }),
    },
  };
}

export function forbiddenValuesRule(forbiddenValues: string[]) {
  return {
    validate: (value: string[]) => {
      return value.some((item) =>
        forbiddenValues.some((value) => item.includes(value)),
      )
        ? i18n.t('UIKit.Inputs.ForbiddenValues', {
            values: forbiddenValues.map((value) => `«${value}»`).join(', '),
          })
        : true;
    },
  };
}

export type NumberType = 'int' | 'long';
export function numberField(type: NumberType = 'int') {
  let options: RegisterOptions = {
    // setValueAs: (v: any) => (v === '' ? undefined : Number(v)),
    valueAsNumber: true,
  };

  switch (type) {
    case 'int':
      options = {
        ...options,
        validate: (value: number | null | undefined) =>
          !value || value <= 2147483647
            ? true
            : i18n.t('UIKit.Inputs.MaxIntNumber'),
      };
      break;
    case 'long':
      options = {
        ...options,
        validate: (value: number) =>
          !value || value <= 8999999999999999
            ? true
            : i18n.t('UIKit.Inputs.MaxLongNumber'),
      };
      break;
  }

  return options as any;
}
