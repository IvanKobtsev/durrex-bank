package nekit.corporation.tariff.domain.usecase

import nekit.corporation.tariff.domain.TariffRepository
import javax.inject.Inject

class GetTariffsUseCase @Inject constructor(
    private val repository: TariffRepository
) {

    suspend operator fun invoke() = repository.getTariffs()
}