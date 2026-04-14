package nekit.corporation.crash.data.model

import kotlinx.serialization.Serializable
import nekit.corporation.crash.BuildConfig

@Serializable
data class CrashLog(
    val service: String = "android-app",
    val environment: String = BuildConfig.BUILD_TYPE,
    val level: String = "error",
    val message: String,
    val exceptionType: String,
    val stackTrace: String,
    val requestMethod: String? = null,
    val requestPath: String? = null,
    val traceId: String? = null,
    val userId: String? = null,
    val fingerprint: String? = null,
    val occurredAtUtc: String,
    val tags: Map<String, String> = emptyMap(),
    val additionalData: Map<String, String?> = emptyMap()
)