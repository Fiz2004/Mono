pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Mono"
include(
    ":app",
    ":data",
    ":common-ui-resources",
    ":feature-calculator",
    ":base",
    ":feature-reminder",
    ":feature-on_boarding",
    ":feature-pin_password",
    ":domain",
    ":feature-input",
    ":feature-calendar",
    ":feature-category_edit",
    ":feature-category_add",
    ":feature-report",
    ":feature-currency",
    ":feature-settings",
    ":base-android",
    ":navigation"
)
