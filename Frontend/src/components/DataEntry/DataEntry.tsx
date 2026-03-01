import styles from "./DataEntry.module.scss";

export interface DataEntryProps {
  title: string | number | null | undefined;
  value: string | number | null | undefined;
}

export function DataEntry({ title, value }: DataEntryProps) {
  return (
    <div className={styles.wrapper}>
      <span className={styles.title}>
        {title === null || title === undefined ? "N/A" : title}
      </span>
      <span className={styles.value}>
        {value === null || value === undefined ? "N/A" : value}
      </span>
    </div>
  );
}
