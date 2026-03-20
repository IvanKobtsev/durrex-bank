package nekit.corporation.profile

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.profile.model.SettingsUi
import nekit.corporation.profile.model.toAccountModel
import nekit.corporation.profile.model.toDomain
import nekit.corporation.profile.model.toUi
import nekit.corporation.profile.mvvm.ProfileInteractions
import nekit.corporation.profile.mvvm.ProfileState
import nekit.corporation.profile.mvvm.UiEvents
import nekit.corporation.profile_impl.R
import nekit.corporation.user.domain.SettingsManager
import nekit.corporation.user.domain.model.Language
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.usecase.GetSettingsUseCase
import nekit.corporation.user.domain.usecase.GetUserUseCase
import nekit.corporation.user.domain.usecase.SaveSettingsUseCase
import nekit.corporation.util.domain.common.NoConnectionFailure
import javax.inject.Inject


internal class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val settingsManager: SettingsManager,
) : StatefulViewModel<ProfileState>(), ProfileInteractions {

    override fun createInitialState(): ProfileState {
        return ProfileState.DEFAULT
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = async { load { getUserUseCase() } }
            val settings = async { load { getSettingsUseCase() } }
            updateState {
                copy(
                    account = user.await()?.toAccountModel(),
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
                    uiFlow.emit(UiEvents.ShowToast(R.string.no_connection))
                } else {
                    uiFlow.emit(UiEvents.ShowToast(R.string.something_went_wrong))
                }
            }
        )
    }

    override val uiFlow: MutableSharedFlow<UiEvents> = MutableSharedFlow()

    override fun onSchemeClick() {
        updateState { copy(isSchemeOpen = true) }
    }

    override fun onSchemeSwitch(scheme: Scheme) {
        updateSetting({ it.copy(scheme = scheme) }, R.string.change_scheme_error)
    }

    override fun onLanguageChange(language: Language) {
        updateSetting({ it.copy(language = language) }, R.string.change_language_error)
    }

    private fun updateSetting(
        transform: (SettingsUi) -> SettingsUi,
        @StringRes errorMessageRes: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSettings = currentScreenState.settings ?: return@launch
            fallback(
                action = { saveSettingsUseCase(transform(currentSettings).toDomain()) },
                onComplete = {
                    updateState { copy(settings = transform(settings!!)) }
                    settingsManager.update { transform(it!!.toUi()).toDomain() }
                },
                onFailure = { uiFlow.emit(UiEvents.ShowToast(errorMessageRes)) }
            )
        }
    }

    override fun onLanguageClick() {
        updateState {
            copy(isLanguageOpen = true)
        }
    }
}