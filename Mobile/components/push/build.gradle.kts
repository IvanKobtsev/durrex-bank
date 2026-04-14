plugins {
    id("android-library-convention")
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.push"
}

dependencies {
    api(stack.retrofit)
    implementation(projects.core.common)
    implementation(platform(stack.firebase))
    implementation(stack.firebase.messaging)
}