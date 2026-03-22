package nekit.corporation.data.remote.interseptors

import android.util.Log
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import nekit.corporation.auth.domain.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val repository: AuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = getRequestWithHeaders(request)

        return getResponseFromRequest(chain, request)
    }

    private fun getRequestWithHeaders(request: Request): Request {
        val requestBuilder = request.newBuilder()

        runBlocking(Dispatchers.IO) {
            val token = repository.getToken()?.token
            Log.d("AuthInterceptor", "Token: $token")
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            } else {
                Log.d("AuthInterceptor", "Token is null, header not added")
            }
        }

        return requestBuilder.build()
    }

    @Suppress("SwallowedException")
    private fun getResponseFromRequest(
        chain: Interceptor.Chain,
        requestWithHeaders: Request
    ): Response =
        chain.proceed(requestWithHeaders)
}
