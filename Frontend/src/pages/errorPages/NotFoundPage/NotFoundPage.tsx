import { ErrorPage } from "components/ErrorPage/ErrorPage.tsx";
import { AppLinks } from "application/constants/appLinks.ts";
import { navigateBack } from "helpers/navigation-helpers.ts";
import { useNavigate } from "react-router-dom";
import styles from "./NotFoundPage.module.scss";

export function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <ErrorPage
      title={"Страница не найдена!"}
      image={<div className={styles.image} />}
      primaryButton={{
        title: "На главную",
        onClick: async () => {
          await navigate(AppLinks.Dashboard.link());
        },
      }}
      secondaryButton={{
        title: "Вернуться",
        onClick: navigateBack,
      }}
    />
  );
}
