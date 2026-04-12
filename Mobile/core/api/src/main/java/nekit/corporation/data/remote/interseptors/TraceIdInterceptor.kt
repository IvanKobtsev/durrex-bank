package nekit.corporation.data.remote.interseptors

import dev.zacsweers.metro.Inject
import okhttp3.Interceptor
import okhttp3.Response
import java.security.SecureRandom
import java.util.UUID

@Inject
class TraceIdInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val traceparent = request.header("traceparent") ?: generate()

        val newRequest = request.newBuilder()
            .header("traceparent", traceparent)
            .build()

        return chain.proceed(newRequest)
    }

    private val random = SecureRandom()

    private fun randomHex(bytes: Int): String {
        val array = ByteArray(bytes)
        random.nextBytes(array)
        return array.joinToString("") { "%02x".format(it) }
    }

    fun generate(): String {
        val version = "00"
        val traceId = randomHex(16)
        val spanId = randomHex(8)
        val traceFlags = "01"

        return "$version-$traceId-$spanId-$traceFlags"
    }

    private companion object {

        private const val HEADER_NAME: String = "traceparent"
    }
}