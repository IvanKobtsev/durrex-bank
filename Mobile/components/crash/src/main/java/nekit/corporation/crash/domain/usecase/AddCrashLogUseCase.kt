package nekit.corporation.crash.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.crash.data.model.CrashLog
import nekit.corporation.crash.domain.MonitoringRepository

@Inject
class AddCrashLogUseCase(
    private val repository: MonitoringRepository
) {

    suspend operator fun invoke(log: CrashLog) {
        repository.addLogs(log)
    }
}