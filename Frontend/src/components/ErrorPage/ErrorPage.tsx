import clsx from "clsx";
import styles from "./ErrorPage.module.scss";
import { Button, type ButtonProps } from "../Button/Button.tsx";
import type { ReactNode } from "react";

interface ErrorPageProps {
  title?: ReactNode;
  message?: ReactNode;
  image?: ReactNode;
  primaryButton?: ButtonProps;
  secondaryButton?: ButtonProps;
}

export function ErrorPage({
  title,
  message,
  image,
  primaryButton,
  secondaryButton,
}: ErrorPageProps) {
  return (
    <div className={styles.ErrorPage}>
      {image}
      {title && <div className={styles.title}>{title}</div>}
      {message && <div className={styles.message}>{message}</div>}
      {primaryButton && (
        <Button
          {...primaryButton}
          className={clsx(styles.primaryButton, primaryButton.className)}
        />
      )}
      {secondaryButton && (
        <Button
          {...secondaryButton}
          className={clsx(styles.primaryButton, secondaryButton.className)}
        />
      )}
    </div>
  );
}
