package nekit.corporation.profile

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.auth_api.AuthApi
import nekit.corporation.loan_shared.domain.usecase.GetRatingUseCase
import nekit.corporation.profile.model.SettingsUi
import nekit.corporation.profile.model.toAccountModel
import nekit.corporation.profile.model.toUi
import nekit.corporation.profile.mvvm.ProfileInteractions
import nekit.corporation.profile.mvvm.ProfileState
import nekit.corporation.profile.mvvm.UiEvents
import nekit.corporation.profile_impl.R
import nekit.corporation.user.domain.SettingsManager
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.usecase.GetSettingsUseCase
import nekit.corporation.user.domain.usecase.GetUserUseCase
import nekit.corporation.user.domain.usecase.UpdateThemeUseCase
import nekit.corporation.util.domain.common.NoConnectionFailure
import java.io.IOException

@Inject
@ViewModelKey(ProfileViewModel::class)
@ContributesIntoMap(
    AppScope::class, binding = binding<ViewModel>()
)
class ProfileViewModel(
    private val authApi: AuthApi,
    private val getUserUseCase: GetUserUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val settingsManager: SettingsManager,
    private val getRatingUseCase: GetRatingUseCase
) : StatefulViewModel<ProfileState>(), ProfileInteractions {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    override fun createInitialState(): ProfileState = ProfileState.DEFAULT

    private var mainJob: Job? = null

    fun init() {
        mainJob?.cancel()

        mainJob = viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                loadProfile()
            } finally {
                updateState {
                    copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun loadProfile() = supervisorScope {
        updateState {
            copy(
                isLoading = true,
                isTechnicBreak = false
            )
        }

        val userDeferred = async { getUserUseCase() }
        val settingsDeferred = async { getSettingsUseCase() }
        val ratingDeferred = async { getRatingUseCase() }

        val user = userDeferred.await()
        val settings = settingsDeferred.await()
        val rating = ratingDeferred.await()

        updateState {
            copy(
                account = user.toAccountModel(rating.rating),
                settings = settings.toUi(),
                isTechnicBreak = false
            )
        }
    }

    override fun onSchemeClick() {
        updateState { copy(isSchemeOpen = !isSchemeOpen) }
    }

    override fun onSchemeSwitch(scheme: Scheme) {
        updateSetting(
            transform = { it.copy(scheme = scheme) },
            errorMessageRes = R.string.change_scheme_error
        )
    }

    override fun onLogoutClick() {
        updateState { copy(isLoading = true) }

        try {
            authApi.getLogoutIntent()?.let { intent ->
                screenEvents.offerEvent(UiEvents.OnLogout(intent))
            }
        } finally {
            updateState {
                copy(
                    isLoading = false,
                    isTechnicBreak = false
                )
            }
        }
    }

    private fun updateSetting(
        transform: (SettingsUi) -> SettingsUi,
        @StringRes errorMessageRes: Int
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            updateState { copy(isLoading = true) }

            try {
                val currentSettings = currentScreenState.settings ?: getSettingsUseCase().toUi()
                val newSettings = transform(currentSettings)

                updateThemeUseCase(newSettings.scheme)
                settingsManager.update(newSettings.scheme)

                updateState {
                    copy(settings = newSettings)
                }

                screenEvents.offerEvent(UiEvents.ChangeTheme)
            } catch (e: Throwable) {
                handleSettingError(e, errorMessageRes)
            } finally {
                updateState {
                    copy(
                        isLoading = false,
                        isTechnicBreak = false
                    )
                }
            }
        }
    }

    private fun handleSettingError(
        throwable: Throwable,
        @StringRes errorMessageRes: Int
    ) {
        when (throwable) {
            is NoConnectionFailure -> screenEvents.offerEvent(UiEvents.ShowToast(R.string.no_connection))
            is IOException -> updateState { copy(isTechnicBreak = true) }
            else -> {
                screenEvents.offerEvent(UiEvents.ShowToast(errorMessageRes))
                Log.d(TAG, "setting error: $throwable")
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        updateState { copy(isTechnicBreak = false) }
        when (throwable) {
            is NoConnectionFailure -> screenEvents.offerEvent(UiEvents.ShowToast(R.string.no_connection))
            is IOException -> updateState { copy(isTechnicBreak = true) }
            else -> {
                screenEvents.offerEvent(UiEvents.ShowToast(R.string.something_went_wrong))
                Log.d(TAG, "error: $throwable")
            }
        }
    }

    fun onLeave() {
        mainJob?.cancel()
        mainJob = null
    }

    private companion object {
        private const val TAG = "ProfileViewModel"
    }
}