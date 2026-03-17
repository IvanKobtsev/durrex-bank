package nekit.corporation.common

import dagger.MapKey
import nekit.corporation.architecture.presentation.StatefulViewModel
import kotlin.reflect.KClass

@Retention
@MapKey
annotation class ViewModelKey(val value: KClass<out StatefulViewModel<*>>)