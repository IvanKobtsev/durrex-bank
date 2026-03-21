plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.shell_main_api"
}
dependencies {
    api(stack.cicerone)
    implementation(stack.androidx.annotation.jvm)
}
