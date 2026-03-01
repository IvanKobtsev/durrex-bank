import styles from "./LoginPage.module.scss";
import { LoginRequest } from "services/user-api/user-api-client.types.ts";
import { Input } from "components/uikit/inputs/Input.tsx";
import {
  registerPassword,
  registerString,
} from "helpers/form/register-helpers.ts";
import { Button } from "components/uikit/buttons/Button.tsx";
import { useLoginMutation } from "services/user-api/user-api-client/Query.ts";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";
import { useAdvancedForm } from "helpers/form/useAdvancedForm.ts";

export function LoginPage() {
  const navigate = useNavigate();
  const loginMutation = useLoginMutation({
    onError: () => {
      toast.error("Ошибка при входе. Проверьте правильность введенных данных.");
    },
    onSuccess: (data) => {
      localStorage.setItem("access_token", data.token ?? "");
      navigate(AppLinks.Dashboard.link());
    },
  });
  const onSubmit = async (data: LoginRequest) => {
    await loginMutation.mutateAsync(data);
  };

  const form = useAdvancedForm<LoginRequest>(onSubmit);

  return (
    <div className={styles.wrapper}>
      <div className={styles.header}>Кабинет сотрудника</div>
      <form className={styles.form} onSubmit={form.handleSubmitDefault}>
        <Input
          {...registerString(form, "username")}
          fieldProps={{ title: "Имя пользователя" }}
        />
        <Input
          {...registerPassword(form, "password")}
          fieldProps={{ title: "Пароль" }}
        />
        <Button type="submit" title={"Войти"} />
      </form>
    </div>
  );
}
