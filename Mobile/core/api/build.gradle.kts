plugins {
    id("android-library-convention")
    alias(stack.plugins.kotlin.ksp)
    `kotlinx-serialization`
}

android {
    namespace = "nekit.corporation.api"
}

dependencies {
    implementation(project(":core:util"))
    implementation(project(":core:common"))
    implementation(projects.core.architecture)
    implementation(projects.feature.auth.authApi)

    implementation(project(":components:account"))
    implementation(project(":components:auth-shared"))
    implementation(project(":components:loan"))
    implementation(project(":components:tariff"))
    implementation(project(":components:user"))

    //network
    implementation(stack.retrofit.mock)
    implementation(stack.logging.interceptor)
    implementation(stack.retrofit.conventer)
    implementation(stack.retrofit)

    //room
    ksp(stack.room.compiler)
    implementation(stack.room)

    //utils
    implementation(stack.kotlinx.serialization.json)


    implementation("com.microsoft.signalr:signalr:10.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.10.2")
}