plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.account_details_api"
}

dependencies {
    api(stack.cicerone)
}
