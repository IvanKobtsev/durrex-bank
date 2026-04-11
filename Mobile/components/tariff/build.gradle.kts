plugins {
    id("android-library-convention")
    `kotlinx-serialization`
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.tariff"
}

dependencies {
    api(stack.retrofit)
    implementation(stack.kotlinx.serialization.json)
    implementation(projects.core.common)
    implementation(projects.core.util)
}