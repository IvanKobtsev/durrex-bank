plugins {
    id("android-application-convention")
    alias(stack.plugins.kotlin.compose)
    alias(stack.plugins.kotlin.serialization)
    alias(stack.plugins.kotlin.ksp)
}

android {
    namespace = "com.example.shift_project"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.shift_project"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        missingDimensionStrategy("version", "dev")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = false
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*"
        }
    }
}

dependencies {
    implementation(stack.lionscribe.libphonenumber)
//    implementation(project(":feature:account-details"))
//    implementation(project(":feature:auth"))
//    implementation(project(":feature:create-loan"))
//    implementation(project(":feature:history"))
//    implementation(project(":feature:language-shared"))
//    implementation(project(":feature:loan-details"))
//    implementation(project(":feature:main"))
//    implementation(project(":feature:onboarding-shared"))
//    implementation(project(":feature:shell-main"))
//    implementation(project(":feature:transaction-details"))
//
//
//    implementation(project(":components:account"))
//    implementation(project(":components:auth-shared"))
//    implementation(project(":components:loan"))
//    implementation(project(":components:tariff"))
//    implementation(project(":components:user"))

    //implementation(project(":core:common"))
    //implementation(project(":core:ui"))
    //implementation(project(":core:architecture"))
    //implementation(project(":core:api"))
    //implementation(project(":core:util"))
    implementation(stack.androidx.fragment.ktx)
    implementation(stack.androidx.ui.tooling)
    implementation(stack.androidx.appcompat)


    //utils
    implementation(stack.kotlinx.serialization.json)
    implementation(stack.gson)
    implementation(stack.androidx.fragment.navigation)
    implementation(stack.androidx.fragment.ui)
    implementation(stack.cicerone)
    //  ksp(stack.dagger.compiler)
    implementation("dev.zacsweers.metro:metrox-android:0.11.4")
    implementation("dev.zacsweers.metro:metrox-viewmodel:0.11.4")
    implementation("dev.zacsweers.metro:metrox-viewmodel-compose:0.11.4")
    implementation("androidx.core:core-ktx:1.18.0")
}