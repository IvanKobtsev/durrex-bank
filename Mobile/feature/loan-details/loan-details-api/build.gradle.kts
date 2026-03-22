plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.loan_details_api"
}
dependencies {
    api(stack.cicerone)
}
