plugins {
    id("android-library-convention")
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.language_component"
}

dependencies {
    implementation(projects.core.common)
}