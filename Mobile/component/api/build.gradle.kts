plugins {
    common.library
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.anvil)
    `kotlinx-serialization`
}

android {
    namespace = "nekit.corporation.api"
}

dependencies {
    implementation(project(":component:util"))
    implementation(project(":component:common"))
    implementation(project(":feature:auth"))
    implementation(project(":components:loan"))
    
    implementation(project(":components:account"))

    //network
    devImplementation(stack.retrofit.mock)
    implementation(stack.logging.interceptor)
    implementation(stack.retrofit.conventer)
    implementation(stack.retrofit)

    //room
    ksp(stack.room.compiler)
    implementation(stack.room)

    //utils
    implementation(stack.kotlinx.serialization.json)

    //DI
    ksp(stack.anvil.utils.compiler)
}

anvil {
    useKsp(contributesAndFactoryGeneration = true)
    generateDaggerFactories = true
}