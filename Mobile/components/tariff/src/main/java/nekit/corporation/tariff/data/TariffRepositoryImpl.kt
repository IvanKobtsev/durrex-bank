package nekit.corporation.tariff.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.tariff.data.remote.TariffApi
import nekit.corporation.tariff.data.remote.model.CreateTariffRequest
import nekit.corporation.tariff.data.remote.model.toDomain
import nekit.corporation.tariff.domain.TariffRepository
import nekit.corporation.tariff.domain.model.Tariff

@Inject
@ContributesBinding(AppScope::class)
class TariffRepositoryImpl(
    private val api: TariffApi
) : TariffRepository {

    override suspend fun getTariffs(): List<Tariff> =
        api.getTariffs().map { it.toDomain() }

    override suspend fun createTariff(
        name: String?,
        interestRate: Double
    ): Tariff = api.createTariff(CreateTariffRequest(name, interestRate)).toDomain()
}