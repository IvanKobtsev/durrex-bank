import styles from "./ErrorBoundary.module.scss";
import rootStyles from "../../root/Root.module.scss";
import { Header } from "../../root/header/Header.tsx";
import { ErrorPage } from "components/ErrorPage/ErrorPage.tsx";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";

export const ErrorBoundary = () => {
  const navigate = useNavigate();

  return (
    <div className={rootStyles.root}>
      <Header />
      <div className={rootStyles.outerWrapper}>
        <div className={rootStyles.pageWrapper}>
          <ErrorPage
            image={<div className={styles.image} />}
            title={"Произошла непредвиденная ошибка!"}
            primaryButton={{
              title: "Перезагрузить",
              onClick: () => document.location.reload(),
            }}
            secondaryButton={{
              title: "На главную",
              onClick: async () => await navigate(AppLinks.Dashboard.link()),
            }}
          />
        </div>
      </div>
    </div>
  );
};
