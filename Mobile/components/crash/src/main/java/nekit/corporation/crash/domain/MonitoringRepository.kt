package nekit.corporation.crash.domain

import nekit.corporation.crash.data.model.CrashLog

interface MonitoringRepository {

    suspend fun sendCrashLogs(logs: List<CrashLog>)

    suspend fun getLatestCrashLogs(): List<CrashLog>

    suspend fun cleanLogs()

    suspend fun addLogs(log: CrashLog)
}