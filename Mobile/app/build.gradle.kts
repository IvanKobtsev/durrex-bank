plugins {
    id("android-application-convention")
    alias(stack.plugins.kotlin.compose)
    alias(stack.plugins.kotlin.serialization)
    alias(stack.plugins.kotlin.ksp)
    alias(stack.plugins.metro)
}

android {
    namespace = "com.example.shift_project"

    defaultConfig {
        applicationId = "com.example.shift_project"
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
    implementation(projects.components.account)
    implementation(projects.components.authShared)
    implementation(projects.components.languageComponent)
    implementation(projects.components.loan)
    implementation(projects.components.onboardingComponent)
    implementation(projects.components.tariff)
    implementation(projects.components.user)

    implementation(projects.core.api)
    implementation(projects.core.architecture)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.util)

    implementation(projects.feature.accountDetails.accountDetailsApi)
    implementation(projects.feature.accountDetails.accountDetailsImpl)
    implementation(projects.feature.auth.authApi)
    implementation(projects.feature.auth.authImpl)
    implementation(projects.feature.createLoan.createLoanApi)
    implementation(projects.feature.createLoan.createLoanImpl)
    implementation(projects.feature.history.historyApi)
    implementation(projects.feature.history.historyImpl)
    implementation(projects.feature.loanDetails.loanDetailsApi)
    implementation(projects.feature.loanDetails.loanDetailsImpl)
    implementation(projects.feature.main.mainApi)
    implementation(projects.feature.main.mainImpl)
    implementation(projects.feature.onboarding.onboardingApi)
    implementation(projects.feature.onboarding.onboardingImpl)
    implementation(projects.feature.profile.profileApi)
    implementation(projects.feature.profile.profileImpl)
    implementation(projects.feature.shellMain.shellMainApi)
    implementation(projects.feature.shellMain.shellMainImpl)
    implementation(projects.feature.transaction.transactionApi)
    implementation(projects.feature.transaction.transactionImpl)
    implementation(projects.feature.transactionDetails.transactionDetailsApi)
    implementation(projects.feature.transactionDetails.transactionDetailsImpl)

    implementation(stack.lionscribe.libphonenumber)

    implementation(stack.androidx.fragment.ktx)
    implementation(stack.androidx.ui.tooling)
    implementation(stack.androidx.appcompat)


    //utils
    implementation(stack.kotlinx.serialization.json)
    implementation(stack.gson)
    implementation(stack.androidx.fragment.navigation)
    implementation(stack.androidx.fragment.ui)
    implementation(stack.cicerone)
    implementation(stack.metro.android)
    implementation(stack.metro.viewmodel)
    implementation(stack.metro.viewmodel.compose)
    implementation("androidx.core:core-ktx:1.18.0")
}