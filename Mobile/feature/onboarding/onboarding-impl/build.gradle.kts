plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.kotlin.compose)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.onboarding_impl"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))

    implementation(projects.components.onboardingComponent)

    implementation(projects.feature.onboarding.onboardingApi)
    implementation(projects.feature.shellMain.shellMainApi)
}