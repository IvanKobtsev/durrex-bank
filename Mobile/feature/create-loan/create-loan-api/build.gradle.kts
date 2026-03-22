plugins {
    id("android-library-convention")
}

android {
    namespace = "nekit.corporation.create_loan_api"
}
dependencies {
    api(stack.cicerone)
}
