package com.example.shift_project.presentation

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.lifecycleScope
import com.example.shift_project.R
import com.example.shift_project.databinding.LaunchScreenBinding
import com.example.shift_project.presentation.App.Companion.INSTANCE
import com.example.shift_project.presentation.model.toLocaleListCompat
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.language_shared.data.datasorce.local.LocaleManager
import nekit.corporation.user.domain.SettingsManager
import java.util.Locale
import kotlin.getValue

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(
    private val fragmentFactory: FragmentFactory,
    private val settingsManager: SettingsManager
) : AppCompatActivity() {

    init {
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    @Inject
    private lateinit var authApi: AuthApi

    override fun attachBaseContext(newBase: Context) {
        PhoneNumberUtil.getInstance(newBase)
        super.attachBaseContext(LocaleManager.getLocalizedContext(newBase))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(
            overrideConfiguration?.let {
                val config = Configuration(it)
                config.setLocale(Locale(LocaleManager.getPersistedLanguage(this)))
                config
            }
        )
    }

    private val ciceroneNavigator by lazy {
        AppNavigator(this, R.id.main_container, supportFragmentManager)
    }
    private lateinit var binding: LaunchScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LaunchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        INSTANCE.navigatorHolder.setNavigator(ciceroneNavigator)
        if (savedInstanceState == null) {
            INSTANCE.router.newRootScreen(authApi.auth())
        }
        lifecycleScope.launch {
            settingsManager.settings.map { it?.language }
                .distinctUntilChanged()
                .collect { language ->
                    language?.let { AppCompatDelegate.setApplicationLocales(it.toLocaleListCompat()) }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        INSTANCE.navigatorHolder.setNavigator(ciceroneNavigator)
    }

    override fun onPause() {
        super.onPause()
        INSTANCE.navigatorHolder.removeNavigator()
    }

    override fun onStart() {
        super.onStart()
    }
}
