package nekit.corporation.data.remote.interseptors

import dev.zacsweers.metro.Inject
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.UnknownFailure
import kotlinx.serialization.json.Json
import nekit.corporation.data.remote.model.ErrorDto
import nekit.corporation.util.domain.common.ForbiddenFailure
import nekit.corporation.util.domain.common.ServerFailure
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection

@Inject
class StatusCodeInterceptor(
    private val json: Json,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.isSuccessful) return response

        if (response.code == 429 || response.code in 500..599) {
            return response
        }

        val errorMessage = response.readErrorMessage(json)

        when (response.code) {
            HttpURLConnection.HTTP_BAD_REQUEST -> {
                response.close()
                throw BadRequestFailure(errorMessage)
            }

            HttpURLConnection.HTTP_UNAUTHORIZED,
            HttpURLConnection.HTTP_FORBIDDEN -> {
                response.close()
                throw ForbiddenFailure(errorMessage)
            }

            HttpURLConnection.HTTP_NOT_FOUND -> {
                response.close()
                throw NotFoundFailure(errorMessage)
            }

            else -> {
                response.close()
                throw UnknownFailure(errorMessage, code = response.code)
            }
        }
    }

    private fun Response.readErrorMessage(json: Json): String? {
        val bodyString = body?.string()
        if (bodyString.isNullOrBlank()) return null

        val contentType = header("Content-Type")
        return if (contentType?.contains("application/json") == true) {
            try {
                json.decodeFromString<ErrorDto>(bodyString).message ?: bodyString
            } catch (_: Exception) {
                bodyString
            }
        } else {
            bodyString
        }
    }
}