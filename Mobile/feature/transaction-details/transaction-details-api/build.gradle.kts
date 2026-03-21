plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.transaction_details_api"
}
dependencies {
    api(stack.cicerone)
}
