package nekit.corporation.architecture.api

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class SignalRHub<Input, Output> {
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(tag, throwable.toString())
        reconnect()
    }
    protected val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + exceptionHandler)

    init {
        scope.launch {
            delay(500)
            connect()
        }
    }

    abstract val inputFlow: Flow<Input>

    abstract val outputFlow: Flow<Result<Output>>

    protected abstract fun reconnect()

    protected abstract fun connect()

    protected abstract val tag: String
}