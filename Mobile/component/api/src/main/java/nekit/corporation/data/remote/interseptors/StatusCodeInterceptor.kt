package nekit.corporation.data.remote.interseptors

import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.UnknownFailure
import kotlinx.serialization.json.Json
import nekit.corporation.data.remote.model.ErrorDto
import nekit.corporation.util.domain.common.ServerFailure
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class StatusCodeInterceptor @Inject constructor(private val json: Json) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) return response

        val responseBody = response.body?.string()
        val contentType = response.header("Content-Type")
        val errorMessage = if (responseBody.isNullOrBlank()) {
            null
        } else {
            if (contentType?.contains("application/json") == true) {
                try {
                    val errorDto = json.decodeFromString<ErrorDto>(responseBody)
                    errorDto.message
                } catch (e: Exception) {
                    responseBody
                }
            } else {
                responseBody
            }
        }
        when (response.code) {
            HttpURLConnection.HTTP_BAD_REQUEST -> throw BadRequestFailure(errorMessage)
            HttpURLConnection.HTTP_NOT_FOUND -> throw NotFoundFailure(errorMessage)
            in BACKEND_FAILURE_STATUS_CODE_FROM..BACKEND_FAILURE_STATUS_CODE_TO -> {
                throw ServerFailure(errorMessage)
            }

            else -> throw UnknownFailure(errorMessage, code = response.code)
        }
    }

    companion object {
        private const val BACKEND_FAILURE_STATUS_CODE_FROM = 500
        private const val BACKEND_FAILURE_STATUS_CODE_TO = 599
    }
}