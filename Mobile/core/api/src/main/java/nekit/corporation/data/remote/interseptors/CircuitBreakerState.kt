package nekit.corporation.data.remote.interseptors

internal enum class CircuitBreakerState {
    CLOSED, OPEN, HALF_OPEN
}