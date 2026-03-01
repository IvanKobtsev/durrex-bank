plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.history"

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

    implementation(project(":feature:language-shared"))
    implementation(project(":components:loan"))
    implementation(project(":components:user"))
    implementation(project(":components:account"))


}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}