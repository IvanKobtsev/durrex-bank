package nekit.corporation.user.domain.model

import nekit.corporation.user.R
enum class Scheme {
    dark,light
}

fun Scheme.getRes() = when (this) {
        Scheme.dark -> R.string.dark
        Scheme.light -> R.string.light
}