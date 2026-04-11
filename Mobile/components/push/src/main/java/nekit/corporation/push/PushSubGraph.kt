package nekit.corporation.push

import dev.zacsweers.metro.GraphExtension

@GraphExtension
interface PushSubGraph {

    fun inject(pushService: PushService)

    @GraphExtension.Factory
    interface Factory {
        fun create(): PushSubGraph
    }
}
