import styles from "./Header.module.scss";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "../../application/constants/appLinks.ts";

export function Header() {
  const navigate = useNavigate();
  return (
    <div
      className={styles.header}
      onClick={() => navigate(AppLinks.Users.link())}
    >
      Durrex Bank
    </div>
  );
}
