plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.main"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    ksp(stack.anvil.utils.compiler)
    api(stack.data.store)
    implementation(stack.kotlinx.serialization.json)

    implementation(project(":core:architecture"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))

    implementation(project(":feature:language-shared"))

    implementation(project(":components:loan"))
    implementation(project(":components:account"))
    implementation(project(":components:user"))

}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}