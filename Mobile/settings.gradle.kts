rootProject.name = "shift_project"

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
        jcenter()
    }
    versionCatalogs {
        register("stack") { from(files("./gradle/stack.versions.toml")) }
    }
}

include(":app")
apply(from = "component/settings-component.gradle.kts")
apply(from = "components/settings-components.gradle.kts")
apply(from = "feature/settings-feature.gradle.kts")
include(":feature:onboarding-shared")
include(":feature:language-shared")
