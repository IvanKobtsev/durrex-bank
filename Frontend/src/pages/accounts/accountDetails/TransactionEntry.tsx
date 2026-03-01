import { TransactionResponse } from "services/core-api/core-api-client.types.ts";
import { DataEntry } from "components/DataEntry/DataEntry.tsx";
import { formatDateRu } from "./AccountDetailsPage.tsx";
import styles from "./AccountDetailsPage.module.scss";
import clsx from "clsx";

export function TransactionEntry({
  transaction,
}: {
  transaction: TransactionResponse;
}) {
  const isPositive = transaction.balanceAfter - transaction.balanceBefore > 0;

  return (
    <div
      className={clsx(
        styles.transaction,
        isPositive ? styles.positive : styles.negative,
      )}
    >
      <DataEntry
        title={"Дата и время"}
        value={formatDateRu(transaction.createdAt)}
      />
      <DataEntry
        title={"Описание"}
        value={translateDescription(transaction.description ?? "N/A")}
      />
      <DataEntry
        title={"Операция"}
        value={transaction.balanceAfter - transaction.balanceBefore}
      />
    </div>
  );
}

const translateDescription = (description: string) => {
  switch (description) {
    case "Credit issuance":
      return "Выдача кредита";
    case "Scheduled payment":
      return "Платёж по кредиту";
    default:
      return description;
  }
};
