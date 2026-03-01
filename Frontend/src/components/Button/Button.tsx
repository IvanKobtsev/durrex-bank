import clsx from "clsx";
import styles from "./Button.module.scss";

export interface ButtonProps {
  id?: string;
  type?: "submit" | "button";
  title?: string;
  className?: string;
  disabled?: boolean;
}

export function Button({ id, type, title, className, disabled }: ButtonProps) {
  return (
    <button
      className={clsx(className, styles.button)}
      id={id}
      type={type}
      disabled={disabled}
    >
      {title}
    </button>
  );
}
