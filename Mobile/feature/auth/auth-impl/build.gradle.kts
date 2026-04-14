plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.auth_impl"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(projects.components.onboardingComponent)

    implementation(projects.core.architecture)
    implementation(projects.core.common)
    implementation(projects.core.util)
    implementation(projects.core.ui)

    implementation(projects.components.authShared)
    implementation(projects.components.account)
    implementation(projects.components.user)
    implementation(projects.components.push)

    implementation(projects.feature.auth.authApi)
    implementation(projects.feature.onboarding.onboardingApi)
    implementation(projects.feature.shellMain.shellMainApi)
    api("net.openid:appauth:0.11.1")
}