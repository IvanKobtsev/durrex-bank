plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.transaction_impl"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.core.architecture)
    implementation(projects.core.common)
    implementation(projects.core.util)
    implementation(projects.core.ui)

    implementation(projects.components.account)
    implementation(projects.components.user)
    implementation("androidx.core:core-ktx:1.18.0")
}