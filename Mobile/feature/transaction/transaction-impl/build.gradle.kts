plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
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

    implementation(projects.feature.transaction.transactionApi)
}