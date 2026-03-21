import { AccountResponse } from "services/core-api/core-api-client.types.ts";
import styles from "pages/users/userCard/UserCard.module.scss";
import { AppLinks } from "application/constants/appLinks.ts";
import { Button } from "components/uikit/buttons/Button.tsx";
import { padWithZeros } from "helpers/string-helpers.tsx";
import {
  accountsAllQueryKey,
  useAccountsDELETEMutation,
} from "services/core-api/core-api-client/Query.ts";
import { toast } from "react-toastify";
import { queryClient } from "services/query-client-helper.ts";
import { EntityCard } from "components/EntityCard/EntityCard.tsx";

export function AccountCard({ account }: { account: AccountResponse }) {
  const closeAccountMutation = useAccountsDELETEMutation(account.id, {
    onError: (error: any) => {
      if (error?.error === "Cannot close account with positive balance.")
        toast.error("Невозможно закрыть счёт с положительным балансом.");
      else toast.error("Ошибка при закрытии счёта.");
    },
    onSuccess: async () => {
      toast.error("Счёт успешно закрыт.");
      await queryClient.invalidateQueries({
        queryKey: accountsAllQueryKey(account.ownerId),
      });
    },
  });

  return (
    <EntityCard
      leftSide={`Счёт #${padWithZeros(account.id.toString(), 10)}`}
      rightSide={
        <>
          <div className={styles.balance}>
            {account.balance} {account.currency}
          </div>
          {!account.closedAt ? (
            <Button
              className={styles.redButton}
              title={"Закрыть"}
              onClick={() => closeAccountMutation.mutate()}
            />
          ) : (
            <div className={styles.balance}>(ЗАКРЫТ)</div>
          )}
        </>
      }
      link={AppLinks.AccountDetails.link({ accountId: account.id! })}
    />
  );
}
