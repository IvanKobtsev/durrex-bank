plugins {
    common.library
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.language_shared"

}

dependencies {
    implementation(project(":component:common"))
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}