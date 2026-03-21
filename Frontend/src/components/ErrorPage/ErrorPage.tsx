import clsx from "clsx";
import styles from "./ErrorPage.module.scss";
import type { ReactNode } from "react";
import { Button, ButtonProps } from "components/uikit/buttons/Button";

interface ErrorPageProps {
  title?: ReactNode;
  image?: ReactNode;
  primaryButton?: ButtonProps;
  secondaryButton?: ButtonProps;
}

export function ErrorPage({
  title,
  image,
  primaryButton,
  secondaryButton,
}: ErrorPageProps) {
  return (
    <div className={styles.ErrorPage}>
      {image}
      {title && <div className={styles.title}>{title}</div>}
      {primaryButton && (
        <Button
          {...primaryButton}
          className={clsx(styles.primaryButton, primaryButton.className)}
        />
      )}
      {secondaryButton && (
        <Button
          {...secondaryButton}
          className={clsx(styles.secondaryButton, secondaryButton.className)}
        />
      )}
    </div>
  );
}
