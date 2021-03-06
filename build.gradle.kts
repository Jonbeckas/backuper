import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    id("com.github.johnrengelman.shadow") version("6.1.0")
}

group = "de.tetraowl"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("ch.swaechter:smbjwrapper:1.1.0")
    implementation("commons-io:commons-io:2.8.0")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}
