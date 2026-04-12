package nekit.corporation.profile.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharedFlow
import nekit.corporation.profile.model.AccountModel
import nekit.corporation.profile.model.SettingsUi
import nekit.corporation.profile.mvvm.ProfileInteractions
import nekit.corporation.profile.mvvm.ProfileState
import nekit.corporation.profile.mvvm.UiEvents
import nekit.corporation.profile_impl.R
import nekit.corporation.ui.component.Headline
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.component.SelectionPanel
import nekit.corporation.ui.component.TechnicBreakScreen
import nekit.corporation.ui.theme.DurexBankTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.getRes

@Composable
internal fun ProfileContent(state: ProfileState, interaction: ProfileInteractions) {
    val colors = LocalAppColors.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgSecondary)
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            Row(
                modifier = Modifier
                    .background(colors.bgPrimary)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Headline(stringResource(R.string.profile))
                Spacer(Modifier.weight(1f))
                IconButton(interaction::onLogoutClick) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_logout),
                        contentDescription = null,
                        tint = colors.iconPrimary
                    )
                }
            }
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
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        if (state.isTechnicBreak) {
            TechnicBreakScreen(modifier = Modifier.fillMaxSize())
        } else if (state.isLoading) {
            LoadingScreen(modifier = Modifier.fillMaxSize())
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
                    rating = 5
                ),
                settings = SettingsUi(
                    scheme = Scheme.dark
                ),
                isSchemeOpen = false,
                isLanguageOpen = false,
                isTechnicBreak = false
            ),
            interaction = object : ProfileInteractions {
                override fun onSchemeSwitch(scheme: Scheme) {
                    TODO("Not yet implemented")
                }

                override fun onLogoutClick() {
                    TODO("Not yet implemented")
                }

                override fun onSchemeClick() {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}