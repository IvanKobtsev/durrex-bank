import { getValueWithCurrency } from "helpers/currency-helper";
import styles from "./AccountActions.module.scss";
import { Button } from "components/uikit/buttons/Button";
import {
  useDepositMutation,
  useWithdrawMutation,
} from "services/core-api/core-api-client/Query.ts";
import { Loading } from "components/uikit/suspense/Loading.tsx";
import { toast } from "react-toastify";

export function AccountActions({
  accountId,
  currency,
}: {
  accountId: number;
  currency: string;
}) {
  const depositMutation = useDepositMutation(accountId);
  const withdrawMutation = useWithdrawMutation(accountId, {
    onError: (error: any) => {
      if (error?.error === "Insufficient funds.")
        toast.error("Недостаточно средств");
    },
  });

  return (
    <div className={styles.accountActions}>
      <Loading
        loading={depositMutation.isPending || withdrawMutation.isPending}
        doNotWrapChildren
      >
        <Button
          className={styles.deposit}
          title={"Внести " + getValueWithCurrency(currency, 100)}
          onClick={() => depositMutation.mutate({ amount: 100 })}
        />
        <Button
          className={styles.withdraw}
          title={"Снять " + getValueWithCurrency(currency, 100)}
          onClick={() => withdrawMutation.mutate({ amount: 100 })}
        />
      </Loading>
    </div>
  );
}
