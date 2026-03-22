import clsx from "clsx";
import styles from "./CreditStatus.module.scss";

export function CreditStatus({ status }: { status: boolean }) {
  return (
    <div
      className={clsx(styles.status, status ? styles.closed : styles.active)}
    >
      {status ? "Закрыт" : "Активен"}
    </div>
  );
}
