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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
import okio.IOException

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

    override fun createInitialState(): ProfileState {
        return ProfileState.DEFAULT
    }

    fun init() {
        Log.d(TAG,"init")
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val user = async { getUserUseCase() }
            val settings = async { getSettingsUseCase() }
            val rating = async { getRatingUseCase() }
            runCatching {
                updateState {
                    copy(
                        account = user.await().toAccountModel(rating.await().rating),
                        settings = settings.await().toUi(),
                        isLoading = false,
                        isTechnicBreak = false
                    )
                }
            }.onFailure {
                Log.d(TAG,"error: $it")
                if (it is IOException) {
                    updateState {
                        copy(isTechnicBreak = true)
                    }
                }
            }

        }
    }

    override fun onSchemeClick() {
        updateState { copy(isSchemeOpen = !isSchemeOpen) }
    }

    override fun onSchemeSwitch(scheme: Scheme) {
        updateState {
            copy(isLoading = true)
        }
        updateSetting({ it.copy(scheme = scheme) }, R.string.change_scheme_error)

    }

    override fun onLogoutClick() {
        updateState { copy(isLoading = true) }
        authApi.getLogoutIntent()?.let {
            screenEvents.offerEvent(UiEvents.OnLogout(it))
        }
        updateState {
            copy(
                isLoading = false,
                isTechnicBreak = false
            )
        }
    }

    private fun updateSetting(
        transform: (SettingsUi) -> SettingsUi,
        @StringRes errorMessageRes: Int
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currentSettings = currentScreenState.settings ?: getSettingsUseCase().toUi()
            try {
                updateThemeUseCase(transform(currentSettings).scheme).let {
                    updateState { copy(settings = transform(settings!!)) }
                    settingsManager.update(transform(currentSettings).scheme)
                    screenEvents.offerEvent(UiEvents.ChangeTheme)
                }
            } catch (_: Throwable) {
                screenEvents.offerEvent(UiEvents.ShowToast(errorMessageRes))
            }
            updateState {
                copy(
                    isLoading = false,
                    isTechnicBreak = false
                )
            }
        }
    }

    fun handleError(throwable: Throwable){
        if (throwable is NoConnectionFailure) {
            screenEvents.offerEvent(UiEvents.ShowToast(R.string.no_connection))
        }
        if (throwable is IOException) {
            updateState {
                copy(isTechnicBreak = true)
            }
        } else {
            screenEvents.offerEvent(UiEvents.ShowToast(R.string.something_went_wrong))
            Log.d(TAG, "error: $throwable")
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}