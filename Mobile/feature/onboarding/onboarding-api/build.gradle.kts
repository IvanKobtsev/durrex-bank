plugins {
    id("android-application-convention")
    alias(stack.plugins.kotlin.compose)
    `kotlin-composecompiler`
}

android {
    namespace = "nekit.corporation.onboarding_api"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    api(stack.cicerone)
    implementation("androidx.core:core-ktx:1.18.0")
}