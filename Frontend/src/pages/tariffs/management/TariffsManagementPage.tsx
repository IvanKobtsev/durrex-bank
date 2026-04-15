import styles from "./TariffsManagementPage.module.scss";
import { useNavigate } from "react-router-dom";
import { Button } from "components/uikit/buttons/Button.tsx";
import { AppLinks } from "application/constants/appLinks.ts";
import { useTariffsAllQuery } from "services/credit-api/credit-api-client/Query.ts";
import { TariffCard } from "../tariffCard/TariffCard.tsx";
import { PageWrapper } from "components/PageWrapper/PageWrapper.tsx";
import { Loading } from "../../../components/uikit/suspense/Loading.tsx";

export function TariffsManagementPage() {
  const navigate = useNavigate();
  const tariffsQuery = useTariffsAllQuery();

  return (
    <PageWrapper>
      <div className={styles.header}>
        Управление тарифами
        <Button
          title={"Новый тариф"}
          onClick={() => navigate(AppLinks.TariffCreation.link())}
        />
      </div>
      <div className={styles.tariffsList}>
        <Loading loading={tariffsQuery.isLoading} doNotWrapChildren>
          {tariffsQuery.data && tariffsQuery.data?.length > 0 ? (
            tariffsQuery.data
              ?.sort((t1, t2) => (t1.id ?? 0) - (t2.id ?? 0))
              .map((t) => <TariffCard key={t.id} tariff={t} />)
          ) : (
            <div className={styles.noTariffs}>Нет тарифов</div>
          )}
        </Loading>
      </div>
    </PageWrapper>
  );
}
