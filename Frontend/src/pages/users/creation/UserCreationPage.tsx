import styles from "./UserCreationPage.module.scss";
import {
  CreateUserRequest,
  CreateUserRequestRole,
} from "services/user-api/user-api-client.types.ts";
import { useForm } from "react-hook-form";
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

export function UserCreationPage() {
  const createUserMutation = useUsersPOSTMutation({
    onError: () => {
      toast.error("Ошибка при создании пользователя.");
    },
    onSuccess: () => {
      toast.success("Пользователь успешно создан.");
    },
  });

  const onSubmit = (data: CreateUserRequest) => {
    createUserMutation.mutate(data);
  };

  const form = useForm<CreateUserRequest>({
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

      <form className={styles.form} onSubmit={form.handleSubmit(onSubmit)}>
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
