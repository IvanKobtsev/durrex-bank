import clsx from "clsx";
import styles from "./CreditDetailsPage.module.scss";
import { useUsersGETQuery } from "services/user-api/user-api-client/Query.ts";
import { UserCard } from "pages/users/userCard/UserCard.tsx";
import { Loading } from "components/uikit/suspense/Loading.tsx";
import { AppLinks } from "application/constants/appLinks.ts";
import { DataEntry } from "components/DataEntry/DataEntry.tsx";
import { useCreditsGETQuery } from "services/credit-api/credit-api-client/Query.ts";
import { padWithZeros } from "helpers/string-helpers.tsx";
import { formatDateRu } from "pages/accounts/accountDetails/AccountDetailsPage.tsx";

export function CreditDetailsPage() {
  const { creditId } = AppLinks.CreditDetails.useParams();

  const { data: credit, isLoading: accountLoading } = useCreditsGETQuery({
    id: creditId,
  });

  const ownerId = credit?.clientId;
  const { data: owner, isLoading: ownerLoading } = useUsersGETQuery(ownerId!, {
    enabled: !!ownerId,
  });

  return (
    <div className={styles.wrapper}>
      <Loading loading={accountLoading}>
        {credit && (
          <div className={clsx(styles.userCard)}>
            <div className={styles.accountHeader}>
              <div className={styles.leftWrapper}>
                <h2>
                  Кредит #{padWithZeros(credit.id?.toString(), 10)} по тарифу{" "}
                  <span>{credit.tariffName}</span>
                </h2>
                {/*{!account.closedAt ? (*/}
                {/*  <span className={styles.status}>{"Открыт"}</span>*/}
                {/*) : (*/}
                {/*  <span className={clsx(styles.status, styles.closed)}>*/}
                {/*    {"Закрыт " + account.closedAt}*/}
                {/*  </span>*/}
                {/*)}*/}
              </div>
            </div>
            <Loading loading={ownerLoading}>
              {owner && <UserCard user={owner} type={"creditor"} />}
            </Loading>
            <div className={styles.accountInfo}>
              <DataEntry
                title={"Дата выдачи"}
                value={formatDateRu(credit.issuedAt ?? "")}
              />
              <DataEntry title={"Сумма кредита"} value={credit.amount} />
              <DataEntry
                title={"Осталось заплатить"}
                value={credit.remainingBalance}
              />
              <span className={styles.history}>История платежей</span>
              {credit.schedule?.map((entry) => (
                <div
                  key={entry.id}
                  className={clsx(
                    styles.scheduleEntry,
                    !entry.isPaid && styles.missed,
                  )}
                >
                  <DataEntry title={"Сумма платежа"} value={entry.amount} />
                  <DataEntry
                    title={"Срок платежа"}
                    value={formatDateRu(entry.dueDate ?? "")}
                  />
                  <DataEntry
                    title={"Оплачен"}
                    value={entry.isPaid ? "Да" : "Нет"}
                  />
                  {entry.isPaid && entry.paidAt && (
                    <DataEntry
                      title={"Дата оплаты"}
                      value={formatDateRu(entry.paidAt)}
                    />
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </Loading>
    </div>
  );
}
