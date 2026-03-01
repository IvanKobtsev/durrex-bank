import clsx from "clsx";
import styles from "./AccountDetailsPage.module.scss";
import { padWithZeros } from "helpers/string-helpers.tsx";
import { useUsersGETQuery } from "services/user-api/user-api-client/Query.ts";
import { UserCard } from "pages/users/userCard/UserCard.tsx";
import { Loading } from "components/uikit/suspense/Loading.tsx";
import { AppLinks } from "application/constants/appLinks.ts";
import {
  useAccountsGETQuery,
  useTransactionsQuery,
} from "services/core-api/core-api-client/Query.ts";
import { DataEntry } from "components/DataEntry/DataEntry.tsx";
import { useState } from "react";
import { Button } from "components/uikit/buttons/Button";
import { TransactionEntry } from "./TransactionEntry";

export function AccountDetailsPage() {
  const { accountId } = AppLinks.AccountDetails.useParams();

  const [page, setPage] = useState(1);
  const pageSize = 20;

  const { data: account, isLoading: accountLoading } = useAccountsGETQuery({
    id: accountId,
  });

  const { data: transactionsHistory, isLoading: transactionsLoading } =
    useTransactionsQuery({
      id: accountId,
      page,
      pageSize,
    });

  const ownerId = account?.ownerId;

  const { data: owner, isLoading: ownerLoading } = useUsersGETQuery(ownerId!, {
    enabled: !!ownerId,
  });

  const totalPages = transactionsHistory?.totalPages ?? 0;

  function goToPage(newPage: number) {
    if (newPage < 1 || newPage > totalPages) return;
    setPage(newPage);
  }

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

            <Loading loading={transactionsLoading}>
              <span className={styles.transactionsTitle}>
                История транзакций
              </span>
              {transactionsHistory && transactionsHistory.items.length > 0 ? (
                <>
                  <div className={styles.transactions}>
                    {transactionsHistory.items.map((tx) => (
                      <TransactionEntry key={tx.id} transaction={tx} />
                    ))}
                  </div>

                  {transactionsHistory.totalPages > 1 && (
                    <div className={clsx(styles.pagination)}>
                      <Button
                        className={clsx(
                          styles.button,
                          page === 1 && styles.disabled,
                        )}
                        title={"Назад"}
                        onClick={() => goToPage(page - 1)}
                        disabled={page === 1}
                      />

                      <span className={styles.text}>
                        Страница {page} из {totalPages}
                      </span>

                      <Button
                        className={clsx(
                          styles.button,
                          page === totalPages && styles.disabled,
                        )}
                        title={"Вперед"}
                        onClick={() => goToPage(page + 1)}
                        disabled={page === totalPages}
                      />
                    </div>
                  )}
                </>
              ) : (
                "Нет истории"
              )}
            </Loading>
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
