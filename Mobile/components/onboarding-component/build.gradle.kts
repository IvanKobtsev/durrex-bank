plugins {
    id("android-library-convention")
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.onboarding_component"
}

dependencies {
    implementation(projects.core.common)
    api(stack.data.store)
    implementation(stack.kotlinx.serialization.json)
}