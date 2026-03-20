plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.kotlin.ksp)
}


android {
    namespace = "nekit.corporation.create_loan_impl"

    buildFeatures{
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
    implementation("androidx.core:core-ktx:1.18.0")
}
