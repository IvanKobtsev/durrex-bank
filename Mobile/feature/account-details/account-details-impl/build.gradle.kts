plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.account_details_impl"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))

    implementation(projects.components.languageComponent)

    implementation(project(":components:loan"))
    implementation(project(":components:account"))
    implementation(project(":components:user"))
    implementation(projects.feature.auth.authApi)
    implementation(projects.feature.accountDetails.accountDetailsApi)
    implementation(projects.feature.shellMain.shellMainApi)
    implementation(projects.feature.transactionDetails.transactionDetailsApi)
}