plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.transaction_details_impl"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(stack.data.store)
    implementation(stack.kotlinx.serialization.json)

    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))

    implementation(project(":components:account"))

    implementation(projects.feature.transactionDetails.transactionDetailsApi)
    implementation(projects.feature.auth.authApi)
}