import { Button } from "components/uikit/buttons/Button.tsx";
import styles from "./UsersManagementPage.module.scss";
import { useUsersAllQuery } from "services/user-api/user-api-client/Query.ts";
import { UserCard } from "../userCard/UserCard.tsx";
import { useNavigate } from "react-router-dom";
import { AppLinks } from "application/constants/appLinks.ts";

export function UsersManagementPage() {
  const navigate = useNavigate();
  const usersQuery = useUsersAllQuery();

  return (
    <div className={styles.wrapper}>
      <div className={styles.header}>
        Управление пользователями
        <Button
          title={"Новый пользователь"}
          onClick={() => navigate(AppLinks.UserCreation.link())}
        />
      </div>
      <div className={styles.usersList}>
        {usersQuery.data?.map((u) => (
          <UserCard user={u} />
        ))}
      </div>
    </div>
  );
}
