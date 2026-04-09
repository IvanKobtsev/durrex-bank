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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
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

@Inject
@ViewModelKey(ProfileViewModel::class)
@ContributesIntoMap(
    AppScope::class, binding = binding<ViewModel>()
)
class ProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val settingsManager: SettingsManager,
    private val getRatingUseCase: GetRatingUseCase
) : StatefulViewModel<ProfileState>(), ProfileInteractions {

    override fun createInitialState(): ProfileState {
        return ProfileState.DEFAULT
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = async { load { getUserUseCase() } }
            val settings = async { load { getSettingsUseCase() } }
            val rating = async { load { getRatingUseCase() } }
            updateState {
                copy(
                    account = user.await()?.toAccountModel(rating.await()?.rating),
                    settings = settings.await()?.toUi(),
                    isLoading = false
                )
            }
        }
    }

    private suspend fun <T> load(action: suspend () -> T): T? {
        return fallback(
            action = { action() },
            onFailure = {
                if (it is NoConnectionFailure) {
                    screenEvents.offerEvent(UiEvents.ShowToast(R.string.no_connection))
                } else {
                    screenEvents.offerEvent(UiEvents.ShowToast(R.string.something_went_wrong))
                    Log.d(TAG, "error: $it")
                }
            }
        )
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

    private fun updateSetting(
        transform: (SettingsUi) -> SettingsUi,
        @StringRes errorMessageRes: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSettings = currentScreenState.settings ?: return@launch
            fallback(
                action = { updateThemeUseCase(transform(currentSettings).scheme) },
                onComplete = {
                    updateState { copy(settings = transform(settings!!)) }
                    settingsManager.update(transform(currentSettings).scheme)
                    screenEvents.offerEvent(UiEvents.ChangeTheme)
                },
                onFailure = {
                    screenEvents.offerEvent(UiEvents.ShowToast(errorMessageRes))
                    Log.d(TAG, "error: $it")
                }
            )
            updateState {
                copy(isLoading = false)
            }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}