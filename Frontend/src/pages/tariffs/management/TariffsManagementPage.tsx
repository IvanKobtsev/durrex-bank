import styles from "./TariffsManagementPage.module.scss";
import { useNavigate } from "react-router-dom";
import { Button } from "components/uikit/buttons/Button.tsx";
import { AppLinks } from "application/constants/appLinks.ts";
import { useTariffsAllQuery } from "services/credit-api/credit-api-client/Query.ts";
import { TariffCard } from "../tariffCard/TariffCard.tsx";

export function TariffsManagementPage() {
  const navigate = useNavigate();
  const tariffsQuery = useTariffsAllQuery();

  return (
    <div className={styles.wrapper}>
      <div className={styles.header}>
        Управление тарифами
        <Button
          title={"Новый тариф"}
          onClick={() => navigate(AppLinks.TariffCreation.link())}
        />
      </div>
      <div className={styles.tariffsList}>
        {tariffsQuery.data
          ?.sort((t1, t2) => (t1.id ?? 0) - (t2.id ?? 0))
          .map((t) => (
            <TariffCard key={t.id} tariff={t} type={"management"} />
          ))}
      </div>
    </div>
  );
}
