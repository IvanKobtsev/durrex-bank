plugins {
    id ("android-library-convention")
}

android {
    namespace = "nekit.corporation.main_api"
}
dependencies {
    api(stack.cicerone)
}
