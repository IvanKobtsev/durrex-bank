package nekit.corporation.onboarding_shared.data.datasource.local.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val isShowedOnboarding: Boolean,
)