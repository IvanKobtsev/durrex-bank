package nekit.corporation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import nekit.corporation.ui.component.StubScreen
import nekit.corporation.banks.R
import nekit.corporation.presentation.BanksViewModel

@Composable
fun BanksScreen(viewModel: BanksViewModel) {
    StubScreen(
        label = stringResource(R.string.service_dont_work),
        description = null,
        buttonText = stringResource(R.string.come_to_main),
        onButtonClick = viewModel::onMainOpen,
        onCloseClick = viewModel::onClose,
        imageRes = R.drawable.sorry_img,
        isFullScreenDrawable = true,
        modifier = Modifier
    )
}
