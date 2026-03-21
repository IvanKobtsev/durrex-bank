plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.profile_api"
}

dependencies {
    api(stack.cicerone)
}