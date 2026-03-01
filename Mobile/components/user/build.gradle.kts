plugins {
    common.library
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.user"

}

dependencies {
    api(stack.retrofit)
    ksp(stack.anvil.utils.compiler)
    implementation(stack.kotlinx.serialization.json)
    implementation(project(":component:common"))
    implementation(project(":component:util"))
}
anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}