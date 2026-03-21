import styles from "./CreditCard.module.scss";
import { padWithZeros } from "helpers/string-helpers.tsx";
import { CreditResponse } from "services/credit-api/credit-api-client.types.ts";
import { EntityCard } from "components/EntityCard/EntityCard.tsx";
import { AppLinks } from "application/constants/appLinks";

export function CreditCard({ credit }: { credit: CreditResponse }) {
  return (
    <EntityCard
      leftSide={`Кредит #${padWithZeros(credit.id!.toString(), 10)}`}
      rightSide={
        <div className={styles.balance}>
          {/*Осталось оплатить: {credit.remainingBalance}*/}
        </div>
      }
      link={AppLinks.CreditDetails.link({ creditId: credit.id! })}
    />
  );
}
