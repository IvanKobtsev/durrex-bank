plugins {
    id("android-library-convention")
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "nekit.corporation.common"
}

dependencies {
    implementation(project(":core:architecture"))
    implementation("androidx.core:core-ktx:1.18.0")
    api(stack.metro.android)
    api(stack.metro.viewmodel)
    api(stack.metro.viewmodel.compose)
    api(stack.androidx.fragment.ktx)
    api(stack.androidx.work)
    implementation(stack.cicerone)
}