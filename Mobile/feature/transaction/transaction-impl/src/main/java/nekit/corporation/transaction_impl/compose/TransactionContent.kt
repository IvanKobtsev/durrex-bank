package nekit.corporation.transaction.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.transaction.model.TransactionInteractions
import nekit.corporation.transaction.model.TransactionState
import nekit.corporation.transaction.R
import nekit.corporation.transaction.model.AccountUi
import nekit.corporation.ui.component.AccountDetailsCard
import nekit.corporation.ui.component.PrimaryInputField
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.component.PrimaryButton
import nekit.corporation.ui.component.SecondaryInputText
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun TransactionContent(state: TransactionState, interactions: TransactionInteractions) {
    val scrollState = rememberScrollState()
    val color = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        if (state.userAccounts.isNotEmpty()) {
            val pagerState = rememberPagerState(0) { state.userAccounts.size }
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
        Spacer(Modifier.weight(1f))
        PrimaryButton(
            text = stringResource(R.string.transfer),
            onClick = interactions::onTransferClick,
            modifier = Modifier
                .fillMaxWidth()
        )
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