plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.auth_impl"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(projects.feature.onboardingShared)

    implementation(projects.core.architecture)
    implementation(projects.core.common)
    implementation(projects.core.util)
    implementation(projects.core.ui)

    implementation(projects.components.authShared)
    implementation(projects.components.account)
    implementation("androidx.core:core-ktx:1.18.0")
}