plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.shell_main"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":feature:main"))
    implementation(project(":feature:history"))

    implementation(project(":component:architecture"))
    implementation(project(":component:common"))
    implementation(project(":component:ui"))

    implementation(stack.cicerone)
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}