plugins {
    common.library
    `kotlin-composecompiler`
    `kotlinx-serialization`
    alias(stack.plugins.anvil)
    alias(stack.plugins.kotlin.ksp)
}


android {
    namespace = "nekit.corporation.loan_processing"

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":component:architecture"))
    implementation(project(":component:common"))
    implementation(project(":component:ui"))
    implementation(project(":component:util"))

    implementation(project(":components:loan"))
    implementation(project(":feature:language-shared"))
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}