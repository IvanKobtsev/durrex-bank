plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "nekit.corporation.ui"

    buildFeatures {
        compose = true
    }
}

dependencies {

    api(platform(stack.androidx.compose.bom))
    api(stack.androidx.compose.animation)
    api(stack.androidx.compose.foundation.layout)
    api(stack.androidx.compose.material)
    api(stack.androidx.compose.material.icons.extended)
    api(stack.androidx.compose.ui.ui)
    api(stack.androidx.material3)
    api(stack.material)
    api(stack.ui)
    api(stack.ui.tooling.preview)
    implementation("androidx.core:core-ktx:1.18.0")
    debugImplementation(stack.ui.tooling)

    implementation(stack.lionscribe.libphonenumber)

    implementation(project(":core:util"))
    implementation(project(":core:common"))

    api(stack.kotlinx.collections.immutable)
}