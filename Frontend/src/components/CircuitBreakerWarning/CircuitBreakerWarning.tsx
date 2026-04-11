import styles from "./CircuitBreakerWarning.module.scss";

type CircuitBreakerWarningProps = {
  remainingMs: number;
};

const toCountdown = (remainingMs: number): string => {
  const remainingSeconds = Math.max(0, Math.ceil(remainingMs / 1_000));
  const minutes = Math.floor(remainingSeconds / 60)
    .toString()
    .padStart(2, "0");
  const seconds = (remainingSeconds % 60).toString().padStart(2, "0");

  return `${minutes}:${seconds}`;
};

export function CircuitBreakerWarning({
  remainingMs,
}: CircuitBreakerWarningProps) {
  return (
    <div className={styles.overlay}>
      <div className={styles.card}>
        <h1>Сервис временно недоступен</h1>
        <p>Попробуйте снова через:</p>
        <div className={styles.countdown}>{toCountdown(remainingMs)}</div>
      </div>
    </div>
  );
}

