import styles from "./ErrorBoundary.module.scss";
import rootStyles from "../../root/Root.module.scss";
import { Header } from "../../root/header/Header.tsx";
import { ErrorPage } from "components/ErrorPage/ErrorPage.tsx";
import { useNavigate, useRouteError } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";
import { useEffect } from "react";
import {
  getUnknownErrorMessage,
  getUnknownErrorStack,
  sendRuntimeErrorToMonitoring,
} from "services/monitoring/monitoring-service.ts";

export const ErrorBoundary = () => {
  const navigate = useNavigate();

  const error = useRouteError() as Error;
  useEffect(() => {
    void sendRuntimeErrorToMonitoring(
      getUnknownErrorMessage(error),
      error.name,
      getUnknownErrorStack(error),
      {
        rejectionType: typeof error,
      },
    );
  }, []);

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
