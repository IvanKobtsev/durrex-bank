plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.auth_api"
}
dependencies {
    api(stack.cicerone)
}
