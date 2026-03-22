import styles from "./LoginPage.module.scss";
import { Button } from "components/uikit/buttons/Button.tsx";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";
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
      <div className={styles.loginWrapper}>
        <Loading loading={auth.isLoading}>
          <div className={styles.header}>Кабинет сотрудника</div>
          <Button
            className={styles.loginButton}
            title={"Войти"}
            onClick={async () => auth.signinRedirect()}
          />
        </Loading>
      </div>
    </div>
  );
}
