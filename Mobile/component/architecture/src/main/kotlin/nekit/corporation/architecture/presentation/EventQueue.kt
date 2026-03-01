package nekit.corporation.architecture.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class EventQueue {

    private val eventsQueue = MutableSharedFlow<Event>(
        onBufferOverflow = BufferOverflow.DROP_LATEST,
        replay = 0,
        extraBufferCapacity = 32
    )

    fun offerEvent(event: Event) {
        if (!eventsQueue.tryEmit(event)) {
            CoroutineScope(Dispatchers.IO).launch {
                eventsQueue.emit(event)
            }
        }
    }

    @Suppress("ComposableEventParameterNaming")
    @Composable
    fun CollectEvent(eventHandler: (Event) -> Unit) {
        LaunchedEffect(key1 = Unit, block = {
            eventsQueue.collect {
                eventHandler(it)
            }
        })
    }

    suspend fun collect(eventHandler: (Event) -> Unit) {
        eventsQueue.collect {
            eventHandler(it)
        }
    }

    @Suppress("ComposableEventParameterNaming")
    @Composable
    fun CollectEventSuspend(eventHandler: suspend (Event) -> Unit) {
        LaunchedEffect(key1 = Unit, block = {
            eventsQueue.collect {
                eventHandler(it)
            }
        })
    }
}
