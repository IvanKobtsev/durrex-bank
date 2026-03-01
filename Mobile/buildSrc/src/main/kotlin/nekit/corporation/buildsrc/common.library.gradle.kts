plugins {
    `android-library`
    `kotlin-android`
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 28
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    flavorDimensions += listOf("version")
    productFlavors {
        create("dev") {
            dimension = "version"
        }
        create("live") {
            dimension = "version"
        }
    }
}