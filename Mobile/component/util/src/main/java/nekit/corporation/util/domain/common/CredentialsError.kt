package nekit.corporation.util.domain.common

sealed class CredentialsError(override val message: String) : Exception() {

    data class Cancelled(override val message: String) : CredentialsError(message)

    data class NoCredentials(override val message: String) : CredentialsError(message)

    data class Failure(override val message: String) : CredentialsError(message)
}
