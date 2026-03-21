plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.history_api"
}
dependencies {
    api(stack.cicerone)
}
