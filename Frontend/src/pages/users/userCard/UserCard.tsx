import styles from "./UserCard.module.scss";
import { UserResponse } from "services/user-api/user-api-client.types.ts";
import { Button } from "components/uikit/buttons/Button.tsx";

export interface UserCardProps {
  user: UserResponse;
}

export function UserCard({ user }: UserCardProps) {
  return (
    <div className={styles.container}>
      <div className={styles.leftWrapper}>
        {user.firstName} {user.lastName} ({user.username}) —{" "}
        {translateRole(user.role)}
      </div>
      <div className={styles.rightWrapper}>
        <Button className={styles.redButton} title={"Заблокировать"} />
        <Button className={styles.redButton} title={"Удалить"} />
      </div>
    </div>
  );
}

const translateRole = (role: UserResponse["role"]) => {
  switch (role) {
    case "Employee":
      return "Сотрудник";
    case "Client":
      return "Клиент";
  }
};
