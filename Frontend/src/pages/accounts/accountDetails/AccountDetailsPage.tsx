import clsx from "clsx";
import styles from "./AccountDetailsPage.module.scss";
import { padWithZeros } from "helpers/string-helpers.tsx";
import { useUsersGETQuery } from "services/user-api/user-api-client/Query.ts";
import { UserCard } from "pages/users/userCard/UserCard.tsx";
import { Loading } from "components/uikit/suspense/Loading.tsx";
import { AppLinks } from "application/constants/appLinks.ts";
import { useAccountsGETQuery } from "services/core-api/core-api-client/Query.ts";
import { DataEntry } from "components/DataEntry/DataEntry.tsx";

export function AccountDetailsPage() {
  const { accountId } = AppLinks.AccountDetails.useParams();

  const { data: account, isLoading: accountLoading } = useAccountsGETQuery({
    id: accountId,
  });

  const ownerId = account?.ownerId;
  const { data: owner, isLoading: ownerLoading } = useUsersGETQuery(ownerId!, {
    enabled: !!ownerId,
  });

  return (
    <div className={styles.wrapper}>
      <Loading loading={accountLoading}>
        {account && (
          <div
            className={clsx(
              styles.userCard,
              !!account.closedAt && styles.closed,
            )}
          >
            <div className={styles.accountHeader}>
              <div className={styles.leftWrapper}>
                <h2>{getAccountName(account.id)}</h2>
                {/*{!account.closedAt ? (*/}
                {/*  <span className={styles.status}>{"Открыт"}</span>*/}
                {/*) : (*/}
                {/*  <span className={clsx(styles.status, styles.closed)}>*/}
                {/*    {"Закрыт " + account.closedAt}*/}
                {/*  </span>*/}
                {/*)}*/}
              </div>
              <div className={styles.balance}>
                {account.balance} {account.currency}
              </div>
            </div>
            <Loading loading={ownerLoading}>
              {owner && <UserCard user={owner} type={"reference"} />}
            </Loading>
            <div className={styles.accountInfo}>
              <DataEntry
                title={"Статус"}
                value={!account.closedAt ? "Открыт" : "Закрыт"}
              />
              <DataEntry
                title={"Создан"}
                value={formatDateRu(account.createdAt) ?? "N/A"}
              />
            </div>
          </div>
        )}
      </Loading>
    </div>
  );
}

export function getAccountName(accountId: number) {
  return `Счёт #${padWithZeros(accountId.toString(), 10)}`;
}

export function formatDateRu(date: Date | string | number): string {
  const d = date instanceof Date ? date : new Date(date);

  return new Intl.DateTimeFormat("ru-RU", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  }).format(d);
}
