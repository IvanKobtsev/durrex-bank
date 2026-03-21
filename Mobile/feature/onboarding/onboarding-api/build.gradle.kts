plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.onboarding_api"
}

dependencies {
    api(stack.cicerone)
}