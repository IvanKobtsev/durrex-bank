plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(stack.android.tools.build)
    implementation(stack.kotlin.gradle)
    implementation(stack.jetbrains.kotlin.serialization)
    implementation(stack.jetbrains.kotlin.compose)
    implementation(stack.jetbrains.compose)
}