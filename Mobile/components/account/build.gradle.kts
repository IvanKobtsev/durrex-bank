plugins {
    id("android-application-convention")
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.account"
}

dependencies {
    api(stack.retrofit)
    implementation(stack.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:util"))
    implementation("androidx.core:core-ktx:1.18.0")
}