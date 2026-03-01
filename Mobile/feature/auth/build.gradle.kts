plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.auth"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    api(stack.credentials.auth)
    api(stack.credentials)
    api(stack.tink)
    api(stack.data.store)

    api(stack.retrofit)
    ksp(stack.anvil.utils.compiler)

    implementation(project(":feature:onboarding-shared"))
    implementation(project(":component:architecture"))
    implementation(project(":component:common"))
    implementation(project(":component:util"))
    implementation(project(":component:ui"))
}
anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}