rootProject.name = "durex_bank"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google ()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        register("stack") { from(files("./gradle/stack.versions.toml")) }
    }
}

include(":app")
apply(from = "core/settings-component.gradle.kts")
apply(from = "components/settings-components.gradle.kts")
apply(from = "feature/settings-feature.gradle.kts")
