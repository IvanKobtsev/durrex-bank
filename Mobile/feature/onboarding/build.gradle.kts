plugins {
    common.library
    `kotlin-composecompiler`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.onboarding"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":component:architecture"))
    implementation(project(":component:common"))
    implementation(project(":component:util"))
    implementation(project(":component:ui"))
    implementation(project(":feature:onboarding-shared"))
    ksp(stack.anvil.utils.compiler)

}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}