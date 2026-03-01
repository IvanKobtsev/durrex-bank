plugins {
    common.library
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.onboarding_shared"
}

dependencies {
    implementation(project(":component:common"))
    ksp(stack.anvil.utils.compiler)
    api(stack.data.store)
    implementation(stack.kotlinx.serialization.json)
}
anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}