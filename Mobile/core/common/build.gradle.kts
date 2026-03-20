plugins {
    id("android-application-convention")
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.common"
}

dependencies {
    implementation(project(":core:architecture"))
    implementation("androidx.core:core-ktx:1.18.0")
}