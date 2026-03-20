plugins {
    id("android-application-convention")
}

android {
    namespace = "nekit.corporation.util"
}

dependencies {
    implementation(stack.androidx.annotation.jvm)
    implementation("androidx.core:core-ktx:1.18.0")
}