import styles from "pages/users/userCard/UserCard.module.scss";
import { AppLinks } from "application/constants/appLinks.ts";
import { Button } from "components/uikit/buttons/Button.tsx";
import { useNavigate } from "react-router-dom";
import { padWithZeros } from "helpers/string-helpers.tsx";
import { useAccountsDELETEMutation } from "services/core-api/core-api-client/Query.ts";
import { toast } from "react-toastify";
import clsx from "clsx";
import { CreditResponse } from "services/credit-api/credit-api-client.types.ts";

export function CreditCard({ credit }: { credit: CreditResponse }) {
  return null;
  // const navigate = useNavigate();
  // const closeAccountMutation = useAccountsDELETEMutation(account.id, {
  //   onError: (error: any) => {
  //     if (error?.error === "Cannot close account with positive balance.")
  //       toast.error("Невозможно закрыть счёт с положительным балансом.");
  //     else toast.error("Ошибка при закрытии счёта.");
  //   },
  //   onSuccess: () => {
  //     toast.error("Счёт успешно закрыт.");
  //   },
  // });
  //
  // return (
  //   <div className={clsx(styles.container, account.closedAt && styles.closed)}>
  //     <div
  //       className={styles.leftWrapper}
  //       onClick={() =>
  //         navigate(AppLinks.AccountDetails.link({ accountId: account.id! }))
  //       }
  //     >
  //       {`Счёт #${padWithZeros(account.id.toString(), 10)}`}
  //     </div>
  //     <div className={styles.rightWrapper}>
  //       <div className={styles.balance}>
  //         {account.balance} {account.currency}
  //       </div>
  //       {!account.closedAt ? (
  //         <Button
  //           className={styles.redButton}
  //           title={"Закрыть"}
  //           onClick={() => closeAccountMutation.mutate()}
  //         />
  //       ) : (
  //         <div className={styles.balance}>(ЗАКРЫТ)</div>
  //       )}
  //     </div>
  //   </div>
  // );
}
