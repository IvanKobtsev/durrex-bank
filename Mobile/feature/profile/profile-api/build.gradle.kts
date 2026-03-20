plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.profile_api"
}

dependencies {
    api(stack.cicerone)
    implementation("androidx.core:core-ktx:1.18.0")
}