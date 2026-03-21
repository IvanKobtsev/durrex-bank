plugins {
    id("android-library-convention")
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.user"
}

dependencies {
    api(stack.retrofit)
    implementation(stack.kotlinx.serialization.json)
    implementation(stack.kotlinx.coroutine)
    implementation(project(":core:common"))
    implementation(project(":core:util"))
}