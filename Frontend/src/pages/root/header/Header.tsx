import styles from "./Header.module.scss";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";
import { Button } from "components/uikit/buttons/Button.tsx";
import { useState } from "react";
import SunIcon from "assets/icons/Sun.svg?react";
import MoonIcon from "assets/icons/Moon.svg?react";

export function Header() {
  const navigate = useNavigate();
  const [isLightTheme, setIsLightTheme] = useState(
    localStorage.getItem("theme") === "light",
  );

  if (isLightTheme) document.documentElement.classList.add("light");
  else document.documentElement.classList.remove("light");
  localStorage.setItem("theme", isLightTheme ? "light" : "dark");

  return (
    <div className={styles.header}>
      <span
        className={styles.logo}
        onClick={() => navigate(AppLinks.Dashboard.link())}
      >
        Durrex Bank
      </span>
      <div className={styles.rightWrapper}>
        <div
          className={styles.themeSwitch}
          onClick={() => setIsLightTheme((prev) => !prev)}
        >
          {isLightTheme ? <SunIcon /> : <MoonIcon />}
        </div>
        <Button
          className={styles.logout}
          title={"Выйти"}
          onClick={() => {
            localStorage.removeItem("access_token");
            navigate(AppLinks.Login.link());
          }}
        />
      </div>
    </div>
  );
}
