package nekit.corporation.crash.data.remote

import nekit.corporation.crash.data.model.CrashLog
import retrofit2.http.Body
import retrofit2.http.POST

interface MonitoringApi {

    @POST("monitoring/api/events")
    suspend fun sendCrashLogs(@Body logs: List<CrashLog>)
}