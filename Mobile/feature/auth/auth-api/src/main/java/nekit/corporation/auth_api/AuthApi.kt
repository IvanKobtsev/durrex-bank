package nekit.corporation.auth_api

import android.content.Intent
import com.github.terrakok.cicerone.Screen

interface AuthApi {

    fun auth(): Screen

    fun getLogoutIntent(): Intent?
}