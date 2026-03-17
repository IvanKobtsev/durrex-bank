plugins {
    common.library
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}
android {
    namespace = "nekit.corporation.auth_shared"
}

dependencies {
    api(stack.credentials.auth)
    api(stack.credentials)
    api(stack.tink)
    api(stack.data.store)

    api(stack.retrofit)
    ksp(stack.anvil.utils.compiler)
    implementation(stack.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
}
anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}