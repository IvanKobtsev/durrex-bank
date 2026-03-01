plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}


android {
    namespace = "nekit.corporation.create_loan"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":component:architecture"))
    implementation(project(":component:common"))
    implementation(project(":component:ui"))
    implementation(project(":component:util"))

    implementation(project(":components:user"))
    implementation(project(":components:account"))
    implementation(project(":components:loan"))
    implementation(project(":components:tariff"))
}
