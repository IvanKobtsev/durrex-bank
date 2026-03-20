plugins {
    id("android-application-convention")
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.onboarding_shared"
}

dependencies {
    implementation(project(":core:common"))
    api(stack.data.store)
    implementation(stack.kotlinx.serialization.json)
    implementation("androidx.core:core-ktx:1.18.0")
}