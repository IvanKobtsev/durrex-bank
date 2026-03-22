package nekit.corporation.profile.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import nekit.corporation.profile.model.AccountModel
import nekit.corporation.profile.model.SettingsUi
import nekit.corporation.profile.mvvm.ProfileInteractions
import nekit.corporation.profile.mvvm.ProfileState
import nekit.corporation.profile.mvvm.UiEvents
import nekit.corporation.profile_impl.R
import nekit.corporation.ui.component.SelectionPanel
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.user.domain.model.Language
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.getRes
import okhttp3.internal.toImmutableList

@Composable
internal fun ProfileContent(state: ProfileState, interaction: ProfileInteractions) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgSecondary)
    ) {
        Spacer(Modifier.height(32.dp))
        AnimatedVisibility(
            visible = state.account != null,
            enter = fadeIn() + slideInVertically { it / 4 },
            exit = fadeOut() + slideOutVertically { -it / 4 }
        ) {
            ProfileCard(
                accountModel = state.account!!,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        AnimatedVisibility(
            visible = state.settings?.language != null,
            enter = fadeIn() + slideInVertically { it / 4 },
            exit = fadeOut() + slideOutVertically { -it / 4 }
        ) {
            SelectionPanel(
                label = stringResource(R.string.selected_language),
                isVisible = true,
                onExpandedChange = interaction::onLanguageClick,
                items = Language.entries.toImmutableList(),
                selectedItem = state.settings?.language,
                onItemSelected = interaction::onLanguageChange,
                itemLabel = { el -> stringResource(el.getRes()) },
            )
        }
        AnimatedVisibility(
            visible = state.settings?.scheme != null,
            enter = fadeIn() + slideInVertically { it / 4 },
            exit = fadeOut() + slideOutVertically { -it / 4 }
        ) {
            SelectionPanel(
                label = stringResource(R.string.selected_theme),
                isVisible = true,
                onExpandedChange = interaction::onSchemeClick,
                items = Scheme.entries.toImmutableList(),
                selectedItem = state.settings?.scheme,
                onItemSelected = interaction::onSchemeSwitch,
                itemLabel = { el -> stringResource(el.getRes()) },
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewProfileContent() {
    DurexBankTheme() {
        ProfileContent(
            state = ProfileState(
                isLoading = true,
                account = AccountModel(
                    firstName = "Ivan",
                    lastName = "Ivanov",
                    email = "william.rufus.day@pet-store.com",
                    phone = "+79999999999",
                    isBlocked = false,
                    rating = 5.0f
                ),
                settings = SettingsUi(
                    language = Language.En,
                    scheme = Scheme.Dark
                ),
                isSchemeOpen = false,
                isLanguageOpen = false
            ),
            interaction = object : ProfileInteractions {
                override val uiFlow: SharedFlow<UiEvents>
                    get() = TODO("Not yet implemented")

                override fun onSchemeSwitch(scheme: Scheme) {
                    TODO("Not yet implemented")
                }

                override fun onSchemeClick() {
                    TODO("Not yet implemented")
                }

                override fun onLanguageChange(language: Language) {
                    TODO("Not yet implemented")
                }

                override fun onLanguageClick() {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}