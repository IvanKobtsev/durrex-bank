import { useEventCallback } from '@mui/material';
import i18n from 'i18next';
import { useState } from 'react';
import { FieldValues } from 'react-hook-form';
import { UseFormSetError } from 'react-hook-form/dist/types/form';
import { DeepPartial } from 'react-hook-form/dist/types/utils';
import { NavigateFunction, useNavigate } from 'react-router';
import { convertToErrorStringInternal } from 'helpers/errorHelpers/convertToErrorStringInternal.ts';
import { textFromErrorKeyValue } from '../errorHelpers/errorToString.ts';

export type UseSendFormReturn<T> = {
  /*
  Function to be passed to form.handleSubmit
   */
  handler: (data: T) => Promise<void>;
  /*
  Server-side error which doesn't belong to any particular field
   */
  overallServerError: string;
  /*
  All server-side errors combined
   */
  serverErrorsCombined: string;
  /*
  Clear errors function, can be used to reset form errors
   */
  clearServerErrors: () => void;
};

export function handleSubmitFormError<T extends FieldValues>(
  error: any,
  setError: UseFormSetError<T>,
): {
  // error not related to any field
  overallServerError: string;
  // field-related and overall error in one string
  formErrorsCombined: string;
} {
  // error could be:
  // - strongly-typed error (if server-side action is decorated with
  // [ProducesResponseType(400, Type = typeof(ValidationProblemDetails))]
  // - untyped error, in which case `error.response` will be populated with response in JSON.
  const errorResponseData = error.response?.data || error.response || error;
  const errors = errorResponseData?.errors;
  let overallError = convertToErrorStringInternal(error);

  // This error means nothing to the user. Should display error about network connection instead.
  if (overallError === "Cannot read properties of undefined (reading 'data')") {
    overallError = i18n.t('Errors.NetworkError');
  }

  if (errors && Object.keys(errors).length) {
    let formErrorsCombined = '';
    // Some field-bound error (e.g. `user with same Name already exists in DB`)
    // (similar code exists in errorHelpers.tsx, don't forget to change it as well)
    Object.entries(errors).forEach((pair) => {
      const { keyInCamelCase, errorText } = textFromErrorKeyValue(pair);

      setError(keyInCamelCase as any, {
        message: errorText,
        type: 'validate',
      });

      formErrorsCombined =
        formErrorsCombined + `${keyInCamelCase}: ${errorText}\n`;
    });
    if (overallError === 'One or more validation errors occurred.') {
      // it doesn't make sense to display this error
      overallError = '';
    }
    if (overallError) {
      formErrorsCombined = overallError + '\n' + formErrorsCombined;
    }

    return {
      formErrorsCombined: formErrorsCombined,
      overallServerError: overallError,
    };
  }

  return {
    overallServerError: overallError,
    formErrorsCombined: overallError,
  };
}

/*
  Helper hook for handling form submit.
  Returns object: {
    handler: (data: TFieldValues) => Promise<void>; // should be passed to handleSubmit function of react-hook-form
    overallServerError // variable containing some general error(not related to any particular field)
                 // that happened during form submitting (e.g. Network error).
                 // Should be rendered somewhere in UI.
  }.

  Usage:
  const submitForm = useErrorHandler(useCallback(async (data: CreateSubtenantDto) => {
    await ClientFactory.TenantClient.createSubtenant(data);
    history.push(sharedRoutes.Subtenants.root);
  }, []), setError);

  alternatively you could create a submitFormFunction completely outside of the component (to get rid of useCallback):
  async function createTenant (data: CreateSubtenantDto, history: H.History) {
    await ClientFactory.TenantClient.createSubtenant(data);
    history.push(sharedRoutes.Subtenants.root);
  }
 */
export function useErrorHandler<TFieldValues extends FieldValues = FieldValues>(
  submitFunction: (
    data: TFieldValues,
    navigate: NavigateFunction,
  ) => Promise<void>,
  setError: UseFormSetError<TFieldValues>,
  reset?: (values?: DeepPartial<TFieldValues>) => void,
): UseSendFormReturn<TFieldValues> {
  const [overallServerError, setOverallServerError] = useState('');
  const [formErrorsCombined, setFormErrorsCombined] = useState('');
  const navigate = useNavigate();

  const submitForm = useEventCallback(async (data: TFieldValues) => {
    try {
      setOverallServerError('');
      setFormErrorsCombined('');
      await submitFunction(data, navigate);
      reset?.(undefined);
    } catch (error) {
      const errorDetails = handleSubmitFormError(error, setError);
      if (errorDetails.formErrorsCombined) {
        setFormErrorsCombined(errorDetails.formErrorsCombined);
      }
      if (errorDetails.overallServerError) {
        setOverallServerError(errorDetails.overallServerError);
      }
    }
  });

  return {
    handler: submitForm,
    overallServerError: overallServerError,
    serverErrorsCombined: formErrorsCombined,
    clearServerErrors: () => {
      setOverallServerError('');
      setFormErrorsCombined('');
    },
  };
}

export function emptyErrorFunction() {
  /*
  Shall be used when errors are handled somewhere else (e.g. inside Loadings)
   */
}
