package com.example.shift_project.presentation.model

import androidx.core.os.LocaleListCompat
import nekit.corporation.user.domain.model.Language
import java.util.Locale

fun Language.toLocaleListCompat(): LocaleListCompat = when(this){

    Language.Ru -> LocaleListCompat.create(Locale("RU"))
    Language.En -> LocaleListCompat.create(Locale("En"))
    Language.Kyr -> LocaleListCompat.create(Locale("ky"))
}