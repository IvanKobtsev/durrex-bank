package nekit.corporation.crash.data.model

import android.util.Log
import java.time.Instant
import java.util.UUID

fun fromThrowable(
    throwable: Throwable,
    threadName: String,
    userId: String
): CrashLog {

    return CrashLog(
        message = throwable.message ?: "Unknown error",
        exceptionType = throwable::class.java.simpleName,
        stackTrace = Log.getStackTraceString(throwable),
        traceId = UUID.randomUUID().toString(),
        userId = userId,
        occurredAtUtc = Instant.now().toString(),
        tags = mapOf(
            "source" to "android",
            "type" to "uncaught-exception",
            "thread" to threadName
        ),
        additionalData = mapOf(
            "cause" to throwable.cause?.javaClass?.simpleName
        ),
        service = "android-app",
        level = "error"
    )
}