plugins {
    id("android-application-convention")
    `kotlin-composecompiler`
}

android {
    namespace = "nekit.corporation.architecture"

    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation(project(":core:util"))
    implementation(stack.gson)
    api(stack.androidx.lifecycle.viewmodel.ktx)
    api(stack.androidx.lifecycle.viewmodel.savedstate)

    implementation(stack.androidx.compose.runtime)
}