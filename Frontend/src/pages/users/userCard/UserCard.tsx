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
import { EntityCard } from "components/EntityCard/EntityCard.tsx";

export interface UserCardProps {
  user: UserResponse;
  type: "management" | "reference" | "creditor";
}

export function UserCard({ user, type = "management" }: UserCardProps) {
  const blockUserMutation = useBlockMutation(user.id!, {
    onError: () => {
      toast.error("Ошибка при блокировке пользователя.");
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: usersAllQueryKey(),
      });
    },
  });
  const unblockUserMutation = useUnblockMutation(user.id!, {
    onError: () => {
      toast.error("Ошибка при разблокировке пользователя.");
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: usersAllQueryKey(),
      });
    },
  });

  return (
    <EntityCard
      leftSide={`${user.firstName} ${user.lastName} (${user.username}) — ${type === "management" ? translateRole(user.role) : type === "creditor" ? "Кредитор" : "Владелец"}`}
      rightSide={
        type === "management" &&
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
        ))
      }
      link={AppLinks.UserDetails.link({ userId: user.id! })}
    />
  );
}

export const translateRole = (role: UserResponse["role"]) => {
  switch (role) {
    case "Employee":
      return "Сотрудник";
    case "Client":
      return "Клиент";
  }
};
