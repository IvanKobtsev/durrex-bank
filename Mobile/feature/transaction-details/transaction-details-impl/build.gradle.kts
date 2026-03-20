plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
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
    implementation("androidx.core:core-ktx:1.18.0")
}