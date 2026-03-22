import styles from "./ThemeSwitcher.module.scss";
import { useEffect, useState } from "react";
import SunIcon from "assets/icons/Sun.svg?react";
import MoonIcon from "assets/icons/Moon.svg?react";
import {
  useSettingsGETQuery,
  useSettingsPUTMutation,
} from "services/web-app-settings-api/web-app-settings-api-client/Query.ts";

export function ThemeSwitcher() {
  const [isLightTheme, _setIsLightTheme] = useState(
    localStorage.getItem("theme") === "light",
  );

  const putSettingsMutation = useSettingsPUTMutation();

  const setIsLightTheme = (isLightTheme: boolean) => {
    _setIsLightTheme(isLightTheme);
    localStorage.setItem("theme", isLightTheme ? "light" : "dark");
    putSettingsMutation.mutate({ theme: isLightTheme ? "light" : "dark" });
  };

  if (isLightTheme) document.documentElement.classList.add("light");
  else document.documentElement.classList.remove("light");

  const { data: settings } = useSettingsGETQuery();

  useEffect(() => {
    if (settings) setIsLightTheme(settings?.theme === "light");
  }, [settings]);

  return (
    <div
      className={styles.themeSwitch}
      onClick={() => setIsLightTheme(!isLightTheme)}
    >
      {isLightTheme ? <SunIcon /> : <MoonIcon />}
    </div>
  );
}
