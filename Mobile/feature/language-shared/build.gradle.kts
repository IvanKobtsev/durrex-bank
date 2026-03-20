plugins {
    id("android-application-convention")
    `kotlinx-serialization`
    //alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.language_shared"
}

dependencies {
    implementation(project(":core:common"))
    implementation("androidx.core:core-ktx:1.18.0")
}