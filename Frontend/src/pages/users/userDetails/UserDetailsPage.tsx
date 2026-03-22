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
  useRatingQuery,
} from "services/credit-api/credit-api-client/Query.ts";
import { CreditCard } from "pages/credits/creditCard/CreditCard.tsx";
import { translateRole } from "../userCard/UserCard.tsx";
import { formatDateRu } from "../../accounts/accountDetails/AccountDetailsPage.tsx";

type CreditRatingTone = "bad" | "warning" | "neutral" | "good" | "excellent";

type CreditRatingMeta = {
  label: string;
  tone: CreditRatingTone;
};

const ratingToneClassByTone: Record<CreditRatingTone, string> = {
  bad: styles.bad,
  warning: styles.warning,
  neutral: styles.neutral,
  good: styles.good,
  excellent: styles.excellent,
};

function getCreditRatingMeta(score: number): CreditRatingMeta {
  if (score <= 400) {
    return { label: "Ужасно", tone: "bad" };
  }

  if (score < 700) {
    return { label: "Плохо", tone: "warning" };
  }

  if (score < 850) {
    return { label: "Ниже среднего", tone: "good" };
  }

  if (score === 850) {
    return { label: "Средний", tone: "neutral" };
  }

  if (score < 900) {
    return { label: "Хороший", tone: "good" };
  }

  if (score < 1000) {
    return { label: "Отличный", tone: "good" };
  }

  return { label: "Превосходный", tone: "excellent" };
}

export function UserDetailsPage() {
  const { userId } = AppLinks.UserDetails.useParams();

  const { data: user, isLoading: userLoading } = useUsersGETQuery(userId);
  const { data: userAccounts, isLoading: accountsLoading } =
    useAccountsAllQuery({ ownerId: userId });
  const { data: userCredits, isLoading: loansLoading } =
    useCreditsAllQuery(userId);
  const { data: userRating } = useRatingQuery({
    clientId: userId,
  });
  const ratingMeta =
    typeof userRating?.score === "number"
      ? getCreditRatingMeta(userRating.score)
      : null;

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
                  {translateRole(user.role)}
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
          <div className={styles.creditsHeader}>
            <h2 className={styles.accountsHeader}>Кредиты</h2>
            {userRating?.calculatedAt && ratingMeta && (
              <div
                className={clsx(
                  styles.creditRating,
                  ratingToneClassByTone[ratingMeta.tone],
                )}
                title={`Рейтинг посчитан ${formatDateRu(userRating?.calculatedAt)}`}
              >
                Кредитный рейтинг: {userRating?.score} ({ratingMeta.label})
              </div>
            )}
          </div>
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
