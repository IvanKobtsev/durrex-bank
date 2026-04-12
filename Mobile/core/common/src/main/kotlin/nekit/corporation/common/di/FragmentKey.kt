package nekit.corporation.common.di

import androidx.fragment.app.Fragment
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class FragmentKey(val value: KClass<out Fragment>)