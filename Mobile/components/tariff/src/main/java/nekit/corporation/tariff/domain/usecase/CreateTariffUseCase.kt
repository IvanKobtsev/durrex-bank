package nekit.corporation.tariff.domain.usecase

import nekit.corporation.tariff.domain.TariffRepository

class CreateTariffUseCase(
    private val repository: TariffRepository
) {

    suspend operator fun invoke(
        name: String?,
        interestRate: Double
    ) = repository.createTariff(name, interestRate)
}