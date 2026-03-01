@file:Suppress("DEPRECATION")

package nekit.corporation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.presentation.model.LoanDetailsState
import nekit.corporation.ui.component.BaseIconButton
import nekit.corporation.ui.component.Caption
import nekit.corporation.ui.component.Headline2
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import nekit.corporation.loan_details.R
import nekit.corporation.models.LoanUiState
import nekit.corporation.presentation.LoanDetailsViewModel
import nekit.corporation.presentation.model.LoanDetailsEvent
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.ui.component.LoadingScreen
import nekit.corporation.utils.getName

@Composable
fun LoanDetailsScreen(viewModel: LoanDetailsViewModel) {
    val colors = LocalAppColors.current
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState
    val context = LocalContext.current

    viewModel.screenEvents.CollectEvent {
        if (it is LoanDetailsEvent) {
            when (it) {
                is LoanDetailsEvent.ShowToast -> Toast.makeText(
                    context,
                    it.textRes,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    when (state) {
        LoanDetailsState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is LoanDetailsState.Content -> {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BaseIconButton(
                        onClick = viewModel::onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(
                                top = WindowInsets.systemBars.asPaddingValues()
                                    .calculateTopPadding()
                            )
                    )
                    Spacer(Modifier.width(16.dp))
                    Headline2(
                        stringResource(R.string.number) + " ${state.credit.id}",
                        color = colors.fontPrimary
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        LoanDetailsCard(
                            loanNumber = state.credit.id,
                            tariff = state.credit.tariffName ?: "",
                            remainBalance = state.credit.remainingBalance,
                            amount = state.credit.amount,
                        )
                    }
                    item {
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.bgPrimary)
                                .padding(16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Caption(
                                    string = stringResource(R.string.state),
                                    color = colors.fontSecondary
                                )
                                Spacer(Modifier.weight(1f))
                                Body2Text(
                                    text = state.credit.status.name,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    state.credit.schedule?.let {
                        items(it) {

                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewLoanDetailsScreen() {
    LoansAppTheme {
        //LoanDetailsScreen()
    }
}