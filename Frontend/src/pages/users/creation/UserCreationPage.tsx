import styles from "./UserCreationPage.module.scss";
import {
  CreateUserRequest,
  CreateUserRequestRole,
} from "services/user-api/user-api-client.types.ts";
import {
  registerPassword,
  registerString,
} from "helpers/form/register-helpers.ts";
import { toast } from "react-toastify";
import { useUsersPOSTMutation } from "services/user-api/user-api-client/Query.ts";
import { Input } from "components/uikit/inputs/Input";
import { Button } from "components/uikit/buttons/Button.tsx";
import { Field } from "components/uikit/Field.tsx";
import React from "react";
import { useAdvancedForm } from "helpers/form/useAdvancedForm.ts";
import { FormError } from "components/uikit/FormError.tsx";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";

export function UserCreationPage() {
  const navigate = useNavigate();
  const createUserMutation = useUsersPOSTMutation({
    onError: () => {
      toast.error("Ошибка при создании пользователя.");
    },
    onSuccess: () => {
      toast.success("Пользователь успешно создан.");
      navigate(AppLinks.Users.link());
    },
  });

  const onSubmit = async (data: CreateUserRequest) => {
    await createUserMutation.mutateAsync(data);
  };

  const form = useAdvancedForm<CreateUserRequest>(onSubmit, {
    defaultValues: {
      email: "",
      username: "",
      password: "",
      firstName: "",
      lastName: "",
      telephoneNumber: "",
      role: CreateUserRequestRole.Client,
      isBlocked: false,
    },
  });

  return (
    <div className={styles.wrapper}>
      <div className={styles.header}>Создание пользователя</div>
      <FormError>{translateOverallError(form.overallError)}</FormError>

      <form className={styles.form} onSubmit={form.handleSubmitDefault}>
        <Input
          {...registerString(form, "email")}
          fieldProps={{ title: "Email" }}
        />

        <Input
          {...registerString(form, "username")}
          fieldProps={{ title: "Имя пользователя" }}
        />

        <Input
          {...registerPassword(form, "password")}
          fieldProps={{ title: "Пароль" }}
        />

        <Input
          {...registerString(form, "firstName")}
          fieldProps={{ title: "Имя" }}
        />

        <Input
          {...registerString(form, "lastName")}
          fieldProps={{ title: "Фамилия" }}
        />

        <Input
          {...registerString(form, "telephoneNumber")}
          fieldProps={{ title: "Телефон" }}
        />

        <Field title={"Роль"}>
          <select {...form.register("role")}>
            <option value="Client">Клиент</option>
            <option value="Employee">Сотрудник</option>
          </select>
        </Field>

        <Button type="submit" title="Создать пользователя" />
      </form>
    </div>
  );
}

const translateOverallError = (error: string | undefined) => {
  switch (error) {
    case "Conflict":
      return "Пользователь с таким email, именем или телефоном уже существует";
    default:
      return error;
  }
};
