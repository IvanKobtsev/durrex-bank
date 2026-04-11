package nekit.corporation.crash.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import nekit.corporation.crash.data.local.CrashLogStore
import nekit.corporation.crash.data.model.CrashLog
import nekit.corporation.crash.data.remote.MonitoringApi
import nekit.corporation.crash.domain.MonitoringRepository

@Inject
@ContributesBinding(AppScope::class)
class MonitoringRepositoryImpl(
    private val crashLogStore: CrashLogStore,
    private val monitoringApi: MonitoringApi
) : MonitoringRepository {

    override suspend fun sendCrashLogs(logs: List<CrashLog>) {
        monitoringApi.sendCrashLogs(logs)
    }

    override suspend fun getLatestCrashLogs(): List<CrashLog> {
        return crashLogStore.readAll()
    }

    override suspend fun cleanLogs() {
        return crashLogStore.clear()
    }

    override suspend fun addLogs(log: CrashLog) {
        return crashLogStore.append(log)
    }
}