import { Navigate, Outlet } from "react-router-dom";
import { Header } from "../header/Header.tsx";
import styles from "./Root.module.scss";
import { AppLinks } from "application/constants/appLinks.ts";

export function Root() {
  if (!localStorage.getItem("access_token")) {
    return <Navigate to={AppLinks.Login.link()} replace />;
  }

  return (
    <div className={styles.root}>
      <Header />
      <div className={styles.wrapper}>
        <Outlet />
      </div>
    </div>
  );
}
