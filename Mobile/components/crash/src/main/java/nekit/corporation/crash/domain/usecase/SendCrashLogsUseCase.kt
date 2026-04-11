package nekit.corporation.crash.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.crash.domain.MonitoringRepository

@Inject
class SendCrashLogsUseCase(
    private val repository: MonitoringRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return try {
            val logs = repository.getLatestCrashLogs()
            repository.sendCrashLogs(logs)
            repository.cleanLogs()
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}