package nekit.corporation.tariff.data.remote.model

import nekit.corporation.tariff.domain.model.CreateTariff
import nekit.corporation.tariff.domain.model.Tariff


fun TariffResponse.toDomain() = Tariff(
    id = id,
    name = name,
    interestRate = interestRate
)

fun CreateTariffRequest.toDomain() = CreateTariff(
    name = name,
    interestRate = interestRate
)
