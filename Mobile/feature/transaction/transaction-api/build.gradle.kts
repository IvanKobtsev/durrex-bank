plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.transaction_api"
}
dependencies {
    api(stack.cicerone)
}
