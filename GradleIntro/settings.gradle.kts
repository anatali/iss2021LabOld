/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/6.7.1/userguide/multi_project_builds.html
 */
/*
println("SETTINGS pluginManagement ...")
pluginManagement {
    repositories {
        //maven { url = uri("file://C:/Users/utente/.m2/repository") }
        mavenLocal()
        gradlePluginPortal()
    }
}*/
println("SETTINGS RUNS ...")
rootProject.name = "demo" //overrides the default behavior of naming the build after the directory it’s in
//include("app") //defines that the build consists of one subproject called app that contains the build logic
//include("taskAccess")
//include("taskCustom")
//include("taskJavaCompile")
//include("taskJavaRun")
//include("taskClasses")
//include("taskForBuildSrc")
//include("taskEvents")
//include("taskProperties")
include("taskForBuildscript")