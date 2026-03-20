plugins {
    id("android-application-convention")
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
}
android {
    namespace = "nekit.corporation.auth_shared"
}

dependencies {
    api(stack.credentials.auth)
    api(stack.credentials)
    api(stack.tink)
    api(stack.data.store)

    api(stack.retrofit)
    implementation(stack.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation("androidx.core:core-ktx:1.18.0")
}