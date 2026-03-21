package nekit.corporation.auth_api

import com.github.terrakok.cicerone.Screen

interface AuthApi {

    fun auth(): Screen
}