plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.kotlin.compose)
}

android {
    namespace = "nekit.corporation.onboarding_impl"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation(project(":feature:onboarding-shared"))
    implementation("androidx.core:core-ktx:1.18.0")
}