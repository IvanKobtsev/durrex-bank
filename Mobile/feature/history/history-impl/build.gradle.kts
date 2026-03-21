plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.history_impl"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))

    implementation(projects.components.languageComponent)
    implementation(project(":components:loan"))
    implementation(project(":components:user"))
    implementation(project(":components:account"))

    implementation(projects.feature.history.historyApi)
    implementation(projects.feature.transactionDetails.transactionDetailsApi)
    implementation(projects.feature.onboarding.onboardingApi)
    implementation(projects.feature.shellMain.shellMainApi)
    implementation(projects.feature.loanDetails.loanDetailsApi)
    implementation(projects.feature.accountDetails.accountDetailsApi)
    implementation(projects.feature.auth.authApi)
}