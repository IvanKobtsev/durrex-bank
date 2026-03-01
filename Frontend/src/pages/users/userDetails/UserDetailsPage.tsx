import { AppLinks } from "application/constants/appLinks.ts";
import { useUsersGETQuery } from "services/user-api/user-api-client/Query.ts";
import { Loading } from "components/uikit/suspense/Loading.tsx";
import clsx from "clsx";
import styles from "./UserDetailsPage.module.scss";
import { DataEntry } from "components/DataEntry/DataEntry.tsx";
import { UserResponseRole } from "services/user-api/user-api-client.types.ts";
import { useAccountsAllQuery } from "services/core-api/core-api-client/Query.ts";
import { AccountCard } from "pages/accounts/accountCard/AccountCard.tsx";
import {
  useCreditsAllQuery,
  useCreditsGETQuery,
} from "../../../services/credit-api/credit-api-client/Query.ts";
import { CreditCard } from "../../credits/creditCard/CreditCard.tsx";

export interface UserDetailsPageProps {}

export function UserDetailsPage(props: UserDetailsPageProps) {
  const { userId } = AppLinks.UserDetails.useParams();

  const { data: user, isLoading: userLoading } = useUsersGETQuery(userId);
  const { data: userAccounts, isLoading: accountsLoading } =
    useAccountsAllQuery({ ownerId: userId });
  const { data: userCredits, isLoading: loansLoading } =
    useCreditsAllQuery(userId);

  return (
    <div className={styles.wrapper}>
      <Loading
        loading={userLoading}
        doNotWrapChildren
        doNotRenderChildrenWhileLoading
      >
        {!!user && (
          <div
            className={clsx(styles.userCard, user.isBlocked && styles.blocked)}
          >
            <div className={styles.userHeader}>
              <h2>{user.username || "Неизвестный пользователь"}</h2>
              {user.role && (
                <span
                  className={clsx(styles.role, {
                    [styles.employee]: user.role === UserResponseRole.Employee,
                    [styles.client]: user.role === UserResponseRole.Client,
                  })}
                >
                  {user.role}
                </span>
              )}
            </div>
            <div className={styles.userInfo}>
              <DataEntry title={"Имя"} value={user.firstName} />
              <DataEntry title={"Фамилия"} value={user.lastName} />
              <DataEntry title={"Email"} value={user.email} />
              <DataEntry title={"Телефон"} value={user.telephoneNumber} />
              {user.isBlocked && (
                <div className={styles.blockedNotice}>
                  Этот пользователь заблокирован
                </div>
              )}
            </div>
          </div>
        )}
      </Loading>
      <Loading
        loading={accountsLoading}
        doNotWrapChildren
        doNotRenderChildrenWhileLoading
      >
        <div className={styles.accounts}>
          <h2 className={styles.accountsHeader}>Счета</h2>
          <div className={styles.accountsList}>
            {userAccounts && userAccounts.length > 0 ? (
              userAccounts.map((a) => <AccountCard account={a} />)
            ) : (
              <div className={styles.userHeader}>Нет счетов</div>
            )}
          </div>
        </div>
      </Loading>
      <Loading
        loading={loansLoading}
        doNotWrapChildren
        doNotRenderChildrenWhileLoading
      >
        <div className={styles.accounts}>
          <h2 className={styles.accountsHeader}>Кредиты</h2>
          <div className={styles.accountsList}>
            {userCredits && userCredits.length > 0 ? (
              userCredits.map((c) => <CreditCard credit={c} />)
            ) : (
              <div className={styles.userHeader}>Нет кредитов</div>
            )}
          </div>
        </div>
      </Loading>
    </div>
  );
}
