pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-kit"
include(":app")
include(":core:common")
include(":core:database")
include(":core:designsystem")
include(":core:media")
include(":core:network")
include(":core:ui")
include(":core:composeui")
include(":kit:video")
include(":ffmpeg:ffmpeg_kit")
// include(":javalib")
