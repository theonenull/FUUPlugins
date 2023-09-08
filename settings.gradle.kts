pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven ("https://jitpack.io" )
        mavenCentral()
        google()
        maven ( "https://developer.huawei.com/repo/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io" )
        mavenCentral()
    }
}

rootProject.name = "FUUPlugins"
include(":app")

