plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.auth"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {

    ksp(stack.anvil.utils.compiler)

    implementation(project(":feature:onboarding-shared"))

    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))

    implementation(project(":components:auth-shared"))
    implementation(project(":components:account"))
}
anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}