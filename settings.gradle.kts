dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven {
            url = java.net.URI("https://customers.pspdfkit.com/maven/")
        }
    }
}
rootProject.name = "ComposePdf"
include(":app")
 