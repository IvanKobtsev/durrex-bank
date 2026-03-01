import styles from "./UserCard.module.scss";
import { UserResponse } from "services/user-api/user-api-client.types.ts";
import { Button } from "components/uikit/buttons/Button.tsx";
import {
  useBlockMutation,
  usersAllQueryKey,
  useUnblockMutation,
} from "services/user-api/user-api-client/Query.ts";
import { toast } from "react-toastify";
import { queryClient } from "services/query-client-helper.ts";
import { AppLinks } from "application/constants/appLinks.ts";
import { useNavigate } from "react-router-dom";

export interface UserCardProps {
  user: UserResponse;
  type: "management" | "reference";
}

export function UserCard({ user, type = "management" }: UserCardProps) {
  const navigate = useNavigate();
  const blockUserMutation = useBlockMutation(user.id!, {
    onError: (error) => {
      toast.error("Ошибка при блокировке пользователя.");
    },
    onSuccess: async (userResponse) => {
      await queryClient.invalidateQueries({
        queryKey: usersAllQueryKey(),
      });
    },
  });
  const unblockUserMutation = useUnblockMutation(user.id!, {
    onError: (error) => {
      toast.error("Ошибка при разблокировке пользователя.");
    },
    onSuccess: async (userResponse) => {
      await queryClient.invalidateQueries({
        queryKey: usersAllQueryKey(),
      });
    },
  });

  return (
    <div className={styles.container}>
      <div
        className={styles.leftWrapper}
        onClick={() =>
          navigate(AppLinks.UserDetails.link({ userId: user.id! }))
        }
      >
        {`${user.firstName} ${user.lastName} (${user.username}) — ${type === "management" ? translateRole(user.role) : "Владелец"}`}
      </div>
      <div className={styles.rightWrapper}>
        {type === "management" &&
          (user.isBlocked ? (
            <Button
              className={styles.redButton}
              title={"Разблокировать"}
              onClick={() => unblockUserMutation.mutateAsync()}
            />
          ) : (
            <Button
              className={styles.redButton}
              title={"Заблокировать"}
              onClick={() => blockUserMutation.mutateAsync()}
            />
          ))}
        {/*<Button className={styles.redButton} title={"Удалить"} />*/}
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
