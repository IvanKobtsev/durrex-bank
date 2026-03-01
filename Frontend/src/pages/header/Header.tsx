import styles from "./Header.module.scss";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";
import { Button } from "components/uikit/buttons/Button.tsx";

export function Header() {
  const navigate = useNavigate();
  return (
    <div className={styles.header}>
      <span
        className={styles.logo}
        onClick={() => navigate(AppLinks.Dashboard.link())}
      >
        Durrex Bank
      </span>
      <Button
        className={styles.logout}
        title={"Выйти"}
        onClick={() => {
          localStorage.removeItem("access_token");
          navigate(AppLinks.Login.link());
        }}
      />
    </div>
  );
}
