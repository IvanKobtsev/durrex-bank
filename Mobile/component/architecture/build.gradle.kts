plugins {
    common.library
    `kotlin-composecompiler`
}

android {
    namespace = "nekit.corporation.architecture"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":component:util"))
    implementation(stack.gson)
    implementation(stack.dagger)
    api(stack.androidx.lifecycle.viewmodel.ktx)
    api(stack.androidx.lifecycle.viewmodel.savedstate)

    implementation(stack.androidx.compose.runtime)
}