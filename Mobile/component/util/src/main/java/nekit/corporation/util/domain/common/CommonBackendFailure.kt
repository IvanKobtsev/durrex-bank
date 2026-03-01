@file:Suppress("MagicNumber")

package nekit.corporation.util.domain.common

import android.annotation.SuppressLint
import java.io.IOException

@SuppressLint("NewApi")
sealed class CommonBackendFailure(
  message: String? = null,
  cause: Throwable? = null,
  val code: Int?,
) : IOException(message ?: cause?.message, cause)

class NoConnectionFailure(
  message: String? = null,
  cause: Throwable? = null,
  code: Int? = null,
) : CommonBackendFailure(message, cause, code)

class NotFoundFailure(
  message: String? = null,
  cause: Throwable? = null
) : CommonBackendFailure(message, cause, 404)

class ServerFailure(
  message: String? = null,
  cause: Throwable? = null
) : CommonBackendFailure(message, cause, 500)

class UnknownFailure(
  message: String? = null,
  cause: Throwable? = null,
  code: Int? = null,
) : CommonBackendFailure(message, cause, code)

class BadRequestFailure(
  message: String? = null,
  cause: Throwable? = null
) : CommonBackendFailure(message, cause, 400)
