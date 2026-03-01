plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.banks"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":component:architecture"))
    implementation(project(":component:common"))
    implementation(project(":component:ui"))
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}