package nekit.corporation.tariff.domain

import nekit.corporation.tariff.domain.model.Tariff

interface TariffRepository {

    suspend fun getTariffs(): List<Tariff>

    suspend fun createTariff(
        name: String?,
        interestRate: Double
    ): Tariff
}