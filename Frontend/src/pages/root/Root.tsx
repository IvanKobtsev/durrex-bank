import { Navigate, Outlet } from "react-router-dom";
import { Header } from "./header/Header.tsx";
import styles from "./Root.module.scss";
import { AppLinks } from "application/constants/appLinks.ts";
import { userData } from "../../main.tsx";
import { useMeQuery } from "services/user-api/user-api-client/Query.ts";

export function Root() {
  if (!localStorage.getItem("access_token")) {
    return <Navigate to={AppLinks.Login.link()} replace />;
  }

  const { data } = useMeQuery();
  userData.userId = data?.id;

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
