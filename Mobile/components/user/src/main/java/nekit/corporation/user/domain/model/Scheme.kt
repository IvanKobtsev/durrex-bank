package nekit.corporation.user.domain.model

import nekit.corporation.user.R
enum class Scheme {
    Dark,Light
}

fun Scheme.getRes() = when (this) {
        Scheme.Dark -> R.string.dark
        Scheme.Light -> R.string.light
}