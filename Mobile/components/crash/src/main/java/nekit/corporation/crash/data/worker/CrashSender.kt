package nekit.corporation.crash.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import nekit.corporation.common.di.MetroWorkerFactory
import nekit.corporation.common.di.WorkerKey
import nekit.corporation.crash.domain.MonitoringRepository
import nekit.corporation.crash.domain.usecase.SendCrashLogsUseCase

@AssistedInject
class CrashSender(
    context: Context,
    @Assisted params: WorkerParameters,
    private val sendCrashLogsUseCase: SendCrashLogsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            sendCrashLogsUseCase()
            Result.success()
        } catch (_: Throwable) {
            Result.failure()
        }
    }

    @WorkerKey(CrashSender::class)
    @ContributesIntoMap(
        AppScope::class,
        binding = binding<MetroWorkerFactory.WorkerInstanceFactory<*>>(),
    )
    @AssistedFactory
    abstract class Factory : MetroWorkerFactory.WorkerInstanceFactory<CrashSender>
}