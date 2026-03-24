package nekit.corporation.common

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
@Inject
class MetroFragmentFactory(private val creators: Map<KClass<out Fragment>, Provider<Fragment>>) :
  FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragmentClass = loadFragmentClass(classLoader, className)
        val creator = creators[fragmentClass.kotlin]

        Log.d("MetroFragmentFactory", "instantiate=$className creator=${creator != null}")

        return try {
            creator?.invoke() ?: super.instantiate(classLoader, className)
        } catch (e: Exception) {
            throw RuntimeException("Error creating fragment $className", e)
        }
    }
}