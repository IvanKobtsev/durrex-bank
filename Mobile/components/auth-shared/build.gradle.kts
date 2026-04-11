plugins {
    id("android-library-convention")
    `kotlinx-serialization`
    alias(stack.plugins.metro)
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
    implementation(projects.core.common)
    implementation(projects.core.util)
    implementation(projects.core.ui)
}