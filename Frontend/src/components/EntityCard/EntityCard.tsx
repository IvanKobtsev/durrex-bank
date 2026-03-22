import { ReactNode } from "react";
import styles from "./EntityCard.module.scss";
import { useNavigate } from "react-router-dom";
import clsx from "clsx";

interface EntityCardProps {
  leftSide: ReactNode;
  rightSide: ReactNode;
  link?: string;
}

export function EntityCard({ leftSide, rightSide, link }: EntityCardProps) {
  const navigate = useNavigate();

  return (
    <div className={styles.container}>
      <div
        className={clsx(styles.leftWrapper, link && styles.clickable)}
        onClick={() => {
          if (link) navigate(link);
        }}
      >
        {leftSide}
      </div>
      <div className={styles.rightWrapper}>{rightSide}</div>
    </div>
  );
}
