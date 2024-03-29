import me.champeau.gradle.JMHPluginExtension
import me.champeau.gradle.JmhBytecodeGeneratorTask
import org.jetbrains.kotlin.gradle.model.AllOpen
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

val serializationVersion = "0.11.1"
val junitVersion: String = "5.3.2"

//val kotlin_version = "1.3.40"
buildscript {
    val kotlin_version = "1.3.40"
    repositories {
        jcenter()
//        maven(url = "https://dl.bintray.com/kotlin/kotlinx")
//        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven (
      url= "https://plugins.gradle.org/m2/"
        )
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlin_version")
         classpath( "me.champeau.gradle:jmh-gradle-plugin:0.4.8")
//        classPath("org.openjdk.jmh:jmh-generator-annprocess")
    }
}

plugins {
    kotlin("jvm") version "1.3.40"
    id("kotlinx-serialization") version "1.3.40"
    id ("me.champeau.gradle.jmh") version "0.4.8"
//    id("kotlinx.benchmark") version "0.2.0-dev-2"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.40"
}
//tasks.withType<allOpen>{
//annotation("org.openjdk.jmh.annotations.State")
//}
configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

configure<JMHPluginExtension>{
     duplicateClassesStrategy = DuplicatesStrategy.WARN
}
//val jmh by tasks.registerin
//tasks.withType<JmhBytecodeGeneratorTask>().doFirst{
//
//}
//jmhJar.doFirst {
//    new File("build/resources/test").mkdirs()
//}

//apply plugin:
group = "com.fudge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://artifactory.cronapp.io/public-release")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
    implementation("com.google.code.gson:gson:2.8.5")
    compile(group = "com.google.code.gson", name = "gson-extras", version = "2.8.5")
    compile("com.squareup:kotlinpoet:1.3.0")
    compile(group = "commons-io", name = "commons-io", version = "2.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M2")
    testCompile("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testCompile(group = "org.jetbrains.kotlin", name = "kotlin-test", version = "1.1.51")
    testCompile("de.danielbechler:java-object-diff:0.95")
    testCompile(group= "org.jetbrains.kotlin", name= "kotlin-reflect", version="1.3.41")
//    implementation ("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:0.2.0" /*, "0.2.0"*/)
//    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:0.2.0-dev-2")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

//sourceSets.create("jmh") {
////    java.srcDirs = ['src/jmh/java']
////    scala.srcDirs = ['src/jmh/scala']
//    resources.srcDirs = ['src/jmh/resources']
//    compileClasspath += sourceSets.main.runtimeClasspath
//}
//
////sourceSets {
//java.sourceSets["jmh"].java {
//    java.srcDirs = ['src/jmh/java']
//    scala.srcDirs = ['src/jmh/scala']
//    resources.srcDirs = ['src/jmh/resources']
//    compileClasspath += sourceSets.main.runtimeClasspath
//}
//}

//benchmark {
//    targets {
//        register("main")
//    }
//}

//sourceSets{
//    jmh {
//        java.srcDirs = ['src/jmh/java']
//        scala.srcDirs = ['src/jmh/scala']
//        resources.srcDirs = ['src/jmh/resources']
//        compileClasspath += sourceSets.main.runtimeClasspath
//    }
//}

//
//tasks.withType<KotlinCompile>().all {
//    kotlinOptions {
////        jvmTarget = '1.6'
//        freeCompilerArgs = freeCompilerArgs + " -Xuse-experimental=kotlin.ExperimentalStdlibApi"
//    }
//
//}