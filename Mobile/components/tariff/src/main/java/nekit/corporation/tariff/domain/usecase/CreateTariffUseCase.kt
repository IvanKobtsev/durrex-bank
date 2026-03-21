package nekit.corporation.tariff.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.tariff.domain.TariffRepository

@Inject
class CreateTariffUseCase(
    private val repository: TariffRepository
) {

    suspend operator fun invoke(
        name: String?,
        interestRate: Double
    ) = repository.createTariff(name, interestRate)
}