import clsx from "clsx";
import styles from "./CreditDetailsPage.module.scss";
import { useUsersGETQuery } from "services/user-api/user-api-client/Query.ts";
import { UserCard } from "pages/users/userCard/UserCard.tsx";
import { Loading } from "components/uikit/suspense/Loading.tsx";
import { AppLinks } from "application/constants/appLinks.ts";
import { DataEntry } from "components/DataEntry/DataEntry.tsx";
import { useCreditsGETQuery } from "services/credit-api/credit-api-client/Query.ts";

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
                <h2>{credit.tariffName}</h2>
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
              {owner && <UserCard user={owner} type={"reference"} />}
            </Loading>
            <div className={styles.accountInfo}>
              <DataEntry title={"Статус"} value={credit.amount} />
            </div>
          </div>
        )}
      </Loading>
    </div>
  );
}
