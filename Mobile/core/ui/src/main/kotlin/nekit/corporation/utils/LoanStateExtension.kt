package nekit.corporation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import nekit.corporation.models.LoanUiState
import nekit.corporation.ui.R
import nekit.corporation.ui.theme.LocalAppColors

@Composable
fun LoanUiState.getColor(): Color {
    val colors = LocalAppColors.current
    return when (this) {
        LoanUiState.APPROVED -> colors.indicatorPositive
        LoanUiState.REGISTERED -> colors.indicatorAttention
        LoanUiState.REJECTED -> colors.indicatorError
    }
}

@Composable
fun LoanUiState.getName() =
    when (this) {
        LoanUiState.APPROVED -> stringResource(R.string.approved)
        LoanUiState.REGISTERED -> stringResource(R.string.registered)
        LoanUiState.REJECTED -> stringResource(R.string.rejected)
    }
