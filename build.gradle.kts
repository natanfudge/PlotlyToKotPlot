import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serializationVersion  ="0.11.1"
buildscript {
    val kotlin_version = "1.3.40"
    repositories {
        jcenter()
    }

    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath ("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    }
}

plugins {
    kotlin("jvm") version "1.3.40"
    id ("kotlinx-serialization") version "1.3.40"
}
//apply plugin:
group = "com.fudge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven ( url  = "https://artifactory.cronapp.io/public-release")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile ("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
    implementation ("com.google.code.gson:gson:2.8.5")
    compile (group = "com.google.code.gson", name =  "gson-extras", version =  "2.8.5")
    compile ("com.squareup:kotlinpoet:1.3.0")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}