import styles from "./TariffCard.module.scss";
import { useNavigate } from "react-router-dom";
import { TariffResponse } from "services/credit-api/credit-api-client.types.ts";

export interface TariffCardProps {
  tariff: TariffResponse;
  type: "management" | "reference";
}

export function TariffCard({ tariff, type = "management" }: TariffCardProps) {
  const navigate = useNavigate();

  return (
    <div className={styles.container}>
      <div
        className={styles.leftWrapper}
        // onClick={() =>
        //   navigate(AppLinks.TariffDetails.link({ tariffId: tariff.id! }))
        // }
      >
        {tariff.name}
      </div>
      <div className={styles.rightWrapper}>
        {`${(tariff.interestRate ?? 0) * 100}% годовых, рассчитан на ${tariff.termMonths} мес.`}
        {/*<Button className={styles.redButton} title={"Удалить"} />*/}
      </div>
    </div>
  );
}
