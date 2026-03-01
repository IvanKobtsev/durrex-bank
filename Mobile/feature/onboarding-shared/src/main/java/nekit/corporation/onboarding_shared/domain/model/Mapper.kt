package nekit.corporation.onboarding_shared.domain.model

import nekit.corporation.onboarding_shared.data.datasource.local.model.Settings

internal fun SettingsModel.toSettings() = Settings(
    isShowedOnboarding = isShowedOnboarding
)

internal fun Settings.toSettingsModel() = SettingsModel(
    isShowedOnboarding = isShowedOnboarding
)