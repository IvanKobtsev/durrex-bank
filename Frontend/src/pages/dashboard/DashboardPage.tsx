import styles from "./DashboardPage.module.scss";
import { Button } from "components/uikit/buttons/Button.tsx";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";
import { PageWrapper } from "components/PageWrapper/PageWrapper.tsx";

export function DashboardPage() {
  const navigate = useNavigate();

  return (
    <PageWrapper>
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
    </PageWrapper>
  );
}
