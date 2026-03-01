import styles from "./DashboardPage.module.scss";
import { Button } from "components/uikit/buttons/Button.tsx";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";

export function DashboardPage() {
  const navigate = useNavigate();

  return (
    <div className={styles.wrapper}>
      <Button
        title={"Управление пользователями"}
        onClick={() => navigate(AppLinks.Users.link())}
      />
      <Button
        title={"Управление тарифами"}
        onClick={() => navigate(AppLinks.Tariffs.link())}
      />
    </div>
  );
}
