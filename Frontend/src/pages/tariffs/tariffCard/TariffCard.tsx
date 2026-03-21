import { TariffResponse } from "services/credit-api/credit-api-client.types.ts";
import { EntityCard } from "components/EntityCard/EntityCard.tsx";

export interface TariffCardProps {
  tariff: TariffResponse;
}

export function TariffCard({ tariff }: TariffCardProps) {
  return (
    <EntityCard
      leftSide={tariff.name}
      rightSide={`${Math.round((tariff.interestRate ?? 0) * 100)}% годовых, рассчитан на ${tariff.termMonths} мес.`}
    />
  );
}
