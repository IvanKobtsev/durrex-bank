package nekit.corporation.data.remote.interseptors

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import jakarta.inject.Inject
import nekit.corporation.util.domain.common.NoConnectionFailure
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor @Inject constructor(
    private val context: Context,
) : Interceptor {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (e: IOException) {
            if (isNetworkAvailable()) {
                throw e
            } else {
                throw NoConnectionFailure()
            }
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
