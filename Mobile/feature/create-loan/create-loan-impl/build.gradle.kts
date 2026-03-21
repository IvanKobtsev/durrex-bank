plugins {
    id("android-library-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}


android {
    namespace = "nekit.corporation.create_loan_impl"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))

    implementation(project(":components:user"))
    implementation(project(":components:account"))
    implementation(project(":components:loan"))
    implementation(project(":components:tariff"))

    implementation(projects.feature.createLoan.createLoanApi)
}
