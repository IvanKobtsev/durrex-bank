package nekit.corporation.crash.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.crash.data.model.CrashLog
import nekit.corporation.crash.domain.MonitoringRepository

@Inject
class SendCrashLogsUseCase(
    private val repository: MonitoringRepository
) {

    suspend operator fun invoke(): Result<List<CrashLog>> {
        return try {
            val logs = repository.getLatestCrashLogs()
            logs.forEach {
                repository.sendCrashLogs(it)
            }
            repository.cleanLogs()
            Result.success(logs)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    /* suspend operator fun invoke(log: CrashLog): Result<List<CrashLog>> {
         return try {
             repository.sendCrashLogs(log)
             Result.success(Unit)
         } catch (e: Throwable) {
             Result.failure(e)
         }
     }*/

    suspend operator fun invoke(log: CrashLog): Result<Unit> {
        return try {
            repository.sendCrashLogs(log)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}