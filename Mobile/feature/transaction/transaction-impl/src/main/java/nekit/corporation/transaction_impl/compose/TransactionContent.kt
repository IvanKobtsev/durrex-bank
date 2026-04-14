package nekit.corporation.transaction_impl.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.transaction_impl.model.TransactionInteractions
import nekit.corporation.transaction.model.TransactionState
import nekit.corporation.transaction.compose.UserDetailsCard
import nekit.corporation.transaction_impl.model.AccountUi
import nekit.corporation.transaction_impl.R
import nekit.corporation.ui.component.AccountDetailsCard
import nekit.corporation.ui.component.PrimaryInputField
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.ui.component.PrimaryButton
import nekit.corporation.ui.component.SecondaryInputText
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun TransactionContent(state: TransactionState, interactions: TransactionInteractions) {
    val scrollState = rememberScrollState()
    val color = LocalAppColors.current
    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            Spacer(Modifier.height(24.dp))

            if (state.userAccounts.isNotEmpty()) {
                val pagerState = rememberPagerState(0) { state.userAccounts.size }
                LaunchedEffect(pagerState) {
                    interactions.onAccountFromChoose(state.userAccounts[pagerState.currentPage].id)
                }
                HorizontalPager(pagerState) {
                    val account = state.userAccounts[it]
                    AccountDetailsCard(account.id, "${account.sum} ${account.currency}")
                }
                Spacer(Modifier.height(32.dp))
            }
            PrimaryInputField(
                value = state.accountTo,
                onValueChange = interactions::onAccountToChange,
                label = stringResource(R.string.transfer_to),
                isError = state.accountToError != null,
                supportingText = state.accountToError?.let { stringResource(it) } ?: "",
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(
                visible = state.recipient != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(32.dp))
                    UserDetailsCard(
                        firstName = state.recipient?.firstName ?: return@AnimatedVisibility,
                        lastName = state.recipient.lastName,
                        phone = state.recipient.phone,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Headline2(
                text = stringResource(R.string.description),
                color = color.fontPrimary
            )
            SecondaryInputText(
                text = state.description,
                onValueChange = interactions::descriptionChange,
                placeholder = stringResource(R.string.description_enter),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(32.dp))
            PrimaryInputField(
                value = state.sum.toString(),
                onValueChange = { interactions.onSumChange(it.toDoubleOrNull() ?: 0.0) },
                label = stringResource(R.string.description_sum),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(32.dp))

            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.transfer),
                onClick = interactions::onTransferClick,
                isEnable = state.isButtonEnable,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        if (state.isLoading)
            LoadingScreen(modifier = Modifier.fillMaxSize())
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewTransactionScreen() {
    TransactionContent(
        state = TransactionState.DEFAULT.copy(
            accountFrom = AccountUi(
                id = 123,
                sum = 123.0,
                currency = "Rub"
            ),
            userAccounts = persistentListOf(
                AccountUi(
                    id = 123,
                    sum = 123.0,
                    currency = "Rub"
                ),
                AccountUi(
                    id = 123,
                    sum = 123.0,
                    currency = "Rub"
                )
            )
        ),
        interactions = object : TransactionInteractions {
            override fun onAccountToChange(id: String) {
                TODO("Not yet implemented")
            }

            override fun onAccountFromChoose(id: Int) {
                TODO("Not yet implemented")
            }

            override fun onSumChange(sum: Double) {
                TODO("Not yet implemented")
            }

            override fun onTransferClick() {
                TODO("Not yet implemented")
            }

            override fun descriptionChange(text: String) {
                TODO("Not yet implemented")
            }
        }
    )
}