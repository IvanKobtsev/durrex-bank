package nekit.corporation.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.ui.component.EnableButton
import nekit.corporation.ui.component.CreditRow
import nekit.corporation.main.R
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun CreditsCard(
    loans: ImmutableList<Credit>,
    onRowClick: (Int) -> Unit,
    onButtonClick: () -> Unit,
) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = colors.bgPrimary
        )
    ) {
        Spacer(Modifier.height(16.dp))

        loans.forEach {
            CreditRow(
                it.id,
                it.tariffName,
                it.amount,
                modifier = Modifier.fillMaxWidth()
            ) { credit ->
                onRowClick(credit)
            }
        }

        EnableButton(
            text = stringResource(R.string.look_all),
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}
