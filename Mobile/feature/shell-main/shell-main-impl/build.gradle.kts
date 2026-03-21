plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.shell_main_impl"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.feature.main.mainApi)
    implementation(projects.feature.history.historyApi)
    implementation(projects.feature.profile.profileApi)
    implementation(projects.feature.shellMain.shellMainApi)

    implementation(projects.core.architecture)
    implementation(projects.core.common)
    implementation(projects.core.ui)

    implementation(stack.cicerone)
}