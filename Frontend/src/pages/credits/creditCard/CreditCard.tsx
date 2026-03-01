import styles from "pages/users/userCard/UserCard.module.scss";
import { AppLinks } from "application/constants/appLinks.ts";
import { useNavigate } from "react-router-dom";
import { padWithZeros } from "helpers/string-helpers.tsx";
import clsx from "clsx";
import { CreditResponse } from "services/credit-api/credit-api-client.types.ts";

export function CreditCard({ credit }: { credit: CreditResponse }) {
  const navigate = useNavigate();

  return (
    <div className={clsx(styles.container)}>
      <div
        className={styles.leftWrapper}
        onClick={() =>
          navigate(AppLinks.CreditDetails.link({ creditId: credit.id! }))
        }
      >
        {`Кредит #${padWithZeros(credit.id!.toString(), 10)}`}
      </div>
      <div className={styles.rightWrapper}>
        <div className={styles.balance}>
          {/*Осталось оплатить: {credit.remainingBalance}*/}
        </div>
      </div>
    </div>
  );
}
