plugins {
    common.library
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.common"
}

dependencies {
    implementation(project(":component:architecture"))
    api(stack.anvil.utils.annotations)
    api(stack.dagger)
    api(stack.anvil.annotations)
}
anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}