package nekit.corporation.user.domain.model

import nekit.corporation.user.R

enum class Language {
    Ru, En, Kyr
}

fun Language.getRes() = when (this) {
    Language.Ru -> R.string.ru
    Language.En -> R.string.en
    Language.Kyr -> R.string.kyr
}

fun Language.getCode() = when(this){
    Language.Ru -> "ru"
    Language.En -> "en"
    Language.Kyr -> "kr"
}