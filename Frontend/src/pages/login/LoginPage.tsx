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
import { Loading } from "../../components/uikit/suspense/Loading.tsx";
import { useAuth } from "react-oidc-context";
import { useEffect } from "react";

export function LoginPage() {
  const navigate = useNavigate();
  const auth = useAuth();

  useEffect(() => {
    if (auth.isAuthenticated) navigate(AppLinks.Dashboard.link());
  }, [auth.isAuthenticated]);

  return (
    <div className={styles.wrapper}>
      <Loading loading={auth.isLoading}>
        <div className={styles.header}>Кабинет сотрудника</div>
        {/*<form className={styles.form} onSubmit={form.handleSubmitDefault}>*/}
        {/*  <Input*/}
        {/*    {...registerString(form, "username")}*/}
        {/*    fieldProps={{ title: "Имя пользователя" }}*/}
        {/*  />*/}
        {/*  <Input*/}
        {/*    {...registerPassword(form, "password")}*/}
        {/*    fieldProps={{ title: "Пароль" }}*/}
        {/*  />*/}
        {/*</form>*/}
        <Button title={"Войти"} onClick={() => auth.signinRedirect()} />
      </Loading>
    </div>
  );
}
