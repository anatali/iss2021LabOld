/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.7.1/userguide/building_java_projects.html
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.72"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use JCenter for resolving dependencies.
    jcenter()
    flatDir{ dirs("../unibolibs")   }
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:29.0-jre")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
//HTTP
    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
//JSON
    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20201115" )

//SOCKET.IO
 // https://mvnrepository.com/artifact/javax.websocket/javax.websocket-api
    implementation("javax.websocket:javax.websocket-api:1.1")   //javax.websocket api is only the specification
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.9")
}

application {
    // Define the main class for the application.
    //mainClass.set("it.unibo.boundaryWalk.AppKt")
    mainClass.set("it.unibo.boundaryWalk.ClientWebsockJavax")
}
