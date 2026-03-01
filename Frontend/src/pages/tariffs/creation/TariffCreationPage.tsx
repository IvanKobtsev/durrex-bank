import styles from "./TariffCreationPage.module.scss";
import { useNavigate } from "react-router-dom";
import { useUsersPOSTMutation } from "services/user-api/user-api-client/Query.ts";
import { toast } from "react-toastify";
import { AppLinks } from "application/constants/appLinks.ts";
import {
  CreateUserRequest,
  CreateUserRequestRole,
} from "services/user-api/user-api-client.types.ts";
import { useAdvancedForm } from "helpers/form/useAdvancedForm.ts";
import { FormError } from "components/uikit/FormError.tsx";
import { Input } from "components/uikit/inputs/Input.tsx";
import {
  registerNumber,
  registerPassword,
  registerString,
} from "helpers/form/register-helpers.ts";
import { Button } from "components/uikit/buttons/Button.tsx";
import React from "react";
import { CreateTariffRequest } from "services/credit-api/credit-api-client.types.ts";
import { useTariffsMutation } from "services/credit-api/credit-api-client/Query.ts";

export function TariffCreationPage() {
  const navigate = useNavigate();
  const createTariffMutation = useTariffsMutation({
    onError: () => {
      toast.error("Ошибка при создании тарифа.");
    },
    onSuccess: () => {
      toast.success("Тариф успешно создан.");
      navigate(AppLinks.Tariffs.link());
    },
  });

  const onSubmit = async (data: CreateTariffRequest) => {
    if (data.interestRate) data.interestRate /= 100;
    await createTariffMutation.mutateAsync(data);
  };

  const form = useAdvancedForm<CreateTariffRequest>(onSubmit);

  return (
    <div className={styles.wrapper}>
      <div className={styles.header}>Создание тарифа</div>
      <FormError>{form.overallError}</FormError>

      <form className={styles.form} onSubmit={form.handleSubmitDefault}>
        <Input
          {...registerString(form, "name", { required: true })}
          fieldProps={{ title: "Название тарифа" }}
        />

        <Input
          {...registerNumber(form, "interestRate", "currency")}
          fieldProps={{ title: "Процентная ставка (процентов)" }}
        />

        <Input
          {...registerNumber(form, "termMonths", "int")}
          fieldProps={{ title: "Срок кредита (месяцев)" }}
        />

        <Button type="submit" title="Создать тариф" />
      </form>
    </div>
  );
}
