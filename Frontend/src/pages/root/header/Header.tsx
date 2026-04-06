import styles from "./Header.module.scss";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";
import { Button } from "components/uikit/buttons/Button.tsx";
import { ThemeSwitcher } from "./ThemeSwitcher.tsx";
import { useAuth } from "react-oidc-context";
import clsx from "clsx";

export function Header() {
  const navigate = useNavigate();
  const auth = useAuth();

  return (
    <div className={styles.header}>
      <span
        className={styles.logo}
        onClick={() => navigate(AppLinks.Dashboard.link())}
      >
        Durrex Bank
      </span>
      <div className={styles.rightWrapper}>
        <ThemeSwitcher />
        <Button
          className={clsx(styles.logout, auth.isLoading && styles.loading)}
          title={"Выйти"}
          onClick={async () => {
            await auth.signoutSilent();
            localStorage.clear();
            sessionStorage.clear();
            navigate(AppLinks.Login.link());
          }}
        />
      </div>
    </div>
  );
}
