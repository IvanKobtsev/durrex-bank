import { Navigate, Outlet } from "react-router-dom";
import { Header } from "./header/Header.tsx";
import styles from "./Root.module.scss";
import { AppLinks } from "application/constants/appLinks.ts";
import { useMeQuery } from "../../services/credit-api/credit-api-client/Query.ts";
import { userData } from "../../main.tsx";

export function Root() {
  if (!localStorage.getItem("access_token")) {
    return <Navigate to={AppLinks.Login.link()} replace />;
  }

  const { data } = useMeQuery();
  userData.userId = data?.clientId;

  return (
    <div className={styles.root}>
      <Header />
      <div className={styles.outerWrapper}>
        <div className={styles.pageWrapper}>
          <Outlet />
        </div>
      </div>
    </div>
  );
}
