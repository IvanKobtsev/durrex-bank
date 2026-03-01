import { Outlet } from "react-router-dom";
import { Header } from "../header/Header.tsx";
import styles from "./Root.module.scss";

export function Root() {
  return (
    <div className={styles.root}>
      <Header />
      <div className={styles.wrapper}>
        <Outlet />
      </div>
    </div>
  );
}
