plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.shell_main_impl"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.feature.main.mainApi)
    implementation(projects.feature.history.historyApi)

    implementation(projects.core.architecture)
    implementation(projects.core.common)
    implementation(projects.core.ui)

    implementation(stack.cicerone)
    implementation("androidx.core:core-ktx:1.18.0")
}