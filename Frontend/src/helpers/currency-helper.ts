export function getValueWithCurrency(
  currency: string | null | undefined,
  value: string | number | undefined,
) {
  const resultValue = value ?? 0;
  let resultCurrency = currency ?? "";

  switch (currency) {
    case "EUR":
      resultCurrency = "€";
      break;
    case "USD":
      resultCurrency = "$";
      break;
    case "RUB":
      resultCurrency = "₽";
      break;
  }

  return resultValue + resultCurrency;
}
