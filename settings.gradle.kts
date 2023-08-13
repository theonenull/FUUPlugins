pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven ("https://jitpack.io" )
        jcenter()
        mavenCentral()
        maven ("https://jitpack.io" )
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
    }
}

rootProject.name = "FUUPlugins"
include(":app")
 