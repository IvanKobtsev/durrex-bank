package nekit.corporation.tariff.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.tariff.domain.TariffRepository

@Inject
class GetTariffsUseCase(
    private val repository: TariffRepository
) {

    suspend operator fun invoke() = repository.getTariffs()
}