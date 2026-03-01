package nekit.corporation.onboarding.presentation.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nekit.corporation.onboarding.OnboardingPage
import nekit.corporation.onboarding.R
import nekit.corporation.onboarding.presentation.model.OnboardingEvent
import nekit.corporation.onboarding.presentation.OnboardingViewModel
import nekit.corporation.ui.component.Body2Text
import nekit.corporation.ui.theme.LoansAppTheme
import nekit.corporation.ui.theme.LocalAppColors
import kotlin.math.absoluteValue

private const val JumpScale = 0.4f
private val DotSpacing: Dp = 8.dp
private val DotSize: Dp = 8.dp

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
) {
    val colors = LocalAppColors.current
    val state = viewModel.screenState.collectAsStateWithLifecycle().value.currentState
    val pagerState = rememberPagerState(
        pageCount = { state.pages.size }
    )

    viewModel.screenEvents.CollectEventSuspend { event ->
        if (event is OnboardingEvent) {
            when (event) {
                is OnboardingEvent.ChangePage -> pagerState.animateScrollToPage(
                    page = event.page,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onSwipe(currentPage = pagerState.currentPage)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
            .systemBarsPadding()
    ) {

        IconButton(
            onClick = viewModel::onSkipClick,
            modifier = Modifier
                .padding(16.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = null,
                tint = colors.iconPrimary,
                modifier = Modifier
                    .align(Alignment.Start)
            )
        }
        Spacer(Modifier.height(16.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val currentPage = state.pages[page]
            OnboardingPage(
                placeholderIdRes = currentPage.image,
                title = stringResource(currentPage.label),
                body = stringResource(currentPage.description)
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val dotSpacingPx = with(LocalDensity.current) { DotSpacing.roundToPx() }
            Points(pagerState, dotSpacingPx, modifier = Modifier.padding(vertical = 16.dp))
            TextButton(
                onClick = viewModel::onContinueClick,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 17.5.dp)
                    .align(Alignment.CenterEnd)
            ) {
                Body2Text(
                    text = if (state.currentPage + 1 != state.pages.size) {
                        stringResource(R.string.next)
                    } else {
                        stringResource(R.string.close)
                    },
                    color = colors.fontPrimary
                )
            }
            if (state.currentPage != 0) {
                TextButton(
                    onClick = viewModel::onBackClick,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 17.5.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Body2Text(
                        text = stringResource(R.string.back),
                        color = colors.fontPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun Points(
    pagerState: PagerState,
    dotSpacingPx: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(DotSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pagerState.pageCount) {
                Box(
                    modifier = Modifier
                        .size(DotSize)
                        .background(color = colors.bgDisable, shape = CircleShape)
                )
            }
        }

        Box(
            Modifier
                .jumpingDotTransition(pagerState, JumpScale, dotSpacingPx)
                .size(DotSize)
                .background(
                    color = colors.iconPrimary,
                    shape = CircleShape,
                )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.jumpingDotTransition(
    pagerState: PagerState,
    jumpScale: Float,
    dotSpacingPx: Int
) = graphicsLayer {
    val pageOffset = pagerState.currentPageOffsetFraction
    val scrollPosition = pagerState.currentPage - pagerState.pageCount / 2 + pageOffset
    translationX = scrollPosition * (size.width + dotSpacingPx)

    val targetScale = jumpScale - 1f

    val absOff = pageOffset.absoluteValue
    val scale = if (absOff < 0.5f) {
        1f + absOff * 2f * targetScale
    } else {
        jumpScale + (1f - absOff * 2f) * targetScale
    }

    scaleX = scale
    scaleY = scale
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewOnboardingScreen() {
    LoansAppTheme {
        //  OnboardingScreen()
    }
}