package com.example.shift_project.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.shift_project.R
import com.example.shift_project.databinding.LaunchScreenBinding
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.firebase.messaging.FirebaseMessaging
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import kotlinx.coroutines.launch
import nekit.corporation.ThemeViewModel
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.common.di.MainNav
import nekit.corporation.language_shared.data.datasorce.local.LocaleManager
import java.util.Locale

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(
    private val fragmentFactory: FragmentFactory,
    private val router: Router,
    @param:MainNav
    private val cicerone: Cicerone<Router>
) : AppCompatActivity() {

    init {
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    private val navigatorHolder get() = cicerone.getNavigatorHolder()

    @Inject
    private lateinit var authApi: AuthApi

    override fun attachBaseContext(newBase: Context) {
        PhoneNumberUtil.getInstance(newBase)
        super.attachBaseContext(LocaleManager.getLocalizedContext(newBase))
    }

    private val ciceroneNavigator by lazy {
        AppNavigator(this, R.id.main_container, supportFragmentManager)
    }
    private lateinit var binding: LaunchScreenBinding

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LaunchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.getResult() != null) {
                    val token = task.getResult()
                    Log.d(TAG, "Token: $token")
                }
            }

        router.newRootScreen(authApi.auth())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                themeViewModel.darkTheme.collect { dark ->
                    AppCompatDelegate.setDefaultNightMode(
                        if (dark) AppCompatDelegate.MODE_NIGHT_YES
                        else AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(ciceroneNavigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    private companion object {
        private const val TAG = "MainActivity"
    }
}
